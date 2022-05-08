<template>
  <div class="container">
    <div class="tableTitle"><span class="midText">借书登记</span></div>
    <br />
    <div>
      <el-form
        :rules="rules"
        class="form1"
        ref="borrow"
        :model="form"
        label-width="80px"
      >
        <el-form-item label="书籍ID" prop="bookId">
          <el-input v-model="form.id"></el-input>
        </el-form-item>
        <el-form-item label="借阅证号" prop="borrowerID">
          <el-input v-model="form.borrowerID"></el-input>
        </el-form-item>
        <el-form-item label="书籍情况">
          <el-select
            v-model="form.situation"
            multiple
            filterable
            allow-create
            style="width:100%"
            placeholder="请选择或输入书籍情况"
          >
            <el-option
              v-for="item in options5"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="书名">
          <el-input :disabled="true" v-model="form.bookName"></el-input>
        </el-form-item>
        <el-form-item label="作者">
          <el-input :disabled="true" v-model="form.author"></el-input>
        </el-form-item>
        <el-form-item label="出版社">
          <el-input :disabled="true" v-model="form.press"></el-input>
        </el-form-item>

        <el-form-item label="书籍状态">
          <div>
            <el-tag
              :type="
                form.bookState === '可借'
                  ? 'success'
                  : form.bookState === '已借出'
                  ? 'danger'
                  : form.bookState === '查无此书'
                  ? 'danger'
                  : ''
              "
              >{{ form.bookState }}</el-tag
            >
          </div>
        </el-form-item>
        <el-form-item label="借阅证状态">
          <div>
            <el-tag
              :type="
                form.borrowerState === '允许借书'
                  ? 'success'
                  : form.borrowerState === '禁止借书'
                  ? 'danger'
                  : form.borrowerState === '未注册'
                  ? 'warning'
                  : ''
              "
              >{{ form.borrowerState }}</el-tag
            >
          </div>
        </el-form-item>
      </el-form>
    </div>
    <el-button class="btn" type="primary" @click="submit">提交</el-button>
    <p class="tips">
      Tips : 在默认用户下登陆务必输入借阅证号<br />书籍情况可多选也可以自定义输入
    </p>
  </div>
</template>
<script>
import axios from "axios";
export default {
  data() {
    //   定义发送获取ID对应书籍信息的方法
    let self = this;
    let getInfo = function() {
      self.getBMsg();
    };
    let checkB = function() {
      self.checkBorrower();
    };
    return {
      form: {
        id: "",
        borrowerID: localStorage.getItem("ms_borrow_id"),
        situation: "",
        bookName: "",
        author: "",
        press: "",

        bookState: "未输入书籍ID",
        borrowerState: "未注册",
      },
      checkList: {
        bookTrue: "",
        borrowerIDTrue: "",
      },
      rules: {
        bookId: [
          {
            required: true,
            message: "请输入书籍ID",
            trigger: "blur",
            validator: getInfo,
          },
        ],
        borrowerID: [
          {
            required: true,
            message: "请输入借阅证ID",
            trigger: "blur",
            validator: checkB,
          },
        ],
      },
      options5: [
        {
          value: "有折页",
          label: "有折页",
        },
        {
          value: "有缺页",
          label: "有缺页",
        },
        {
          value: "有污损",
          label: "有污损",
        },
      ],
    };
  },
  methods: {
    submit() {
      console.log("submit!");
      // this.$refs.borrow.validate((valid) => {
      //   if (valid) {
      //     console.log("信息有效");

      //信息有效
      if (this.form.bookState !== "可借") {
        alert("[借书失败]书籍不存在或已被借出！");
        return;
      }
      if (this.form.borrowerState !== "允许借书") {
        alert("[借书失败]当前账号无法借书！请联系管理员");
        return;
      }
      console.log("发送借书请求");
      axios
        .post("http://127.0.0.1:5000" + "/borrow_book3", this.form)
        .then((Response) => {
          if (Response.data.true) {
            this.$message({
              message: "[借书成功]成功借书！请在规定时间内归还！",
              type: "success",
            });
            // 借书成功后刷新信息
            this.form.id = null;
            this.form.bookState = "未输入书籍ID";
            this.checkBorrower(); //获取借阅证状态
          } else {
            alert("[借书失败]提交请求失败，请联系管理员");
          }
        });

      // });
    },
    getBMsg() {
      if (this.form.id == "") {
        this.$message({
          message: "请输入书籍ID",
          type: "error",
        });
        return;
      }
      // 填写完书籍ID立即发送请求
      axios
        .post("http://127.0.0.1:5000" + "/borrow_book1", this.form)
        .then((Response) => {
          console.log(Response);
          (this.checkList.bookTrue = Response.data.bookTrue),
            (this.form.bookName = Response.data.bookName),
            (this.form.author = Response.data.author),
            (this.form.press = Response.data.press),
            (this.form.library = Response.data.library),
            (this.form.bookState = Response.data.bookState);
        })
        .catch((error) => {
          alert("检查书籍ID失败！请联系管理员");
          console.log(error);
          return;
        })
        .then(() => {
          if (this.checkList.bookTrue) {
            return;
          } else {
            // console.log(this.checkPass.true);
            this.$message({
              message: "未查询到此书！",
              type: "error",
            });
          }
        });
    },
    checkBorrower() {
      if (this.form.borrowerID == null) {
        this.$message({
          message: "请输入借阅证ID",
          type: "error",
        });
        return;
      }
      axios
        .post("http://127.0.0.1:5000" + "/borrow_book2", this.form)
        .then((Response) => {
          console.log("接口2请求");
          console.log(Response);
          (this.checkList.borrowerIDTrue = Response.data.borrowerIDTrue),
            (this.form.borrowerState = Response.data.borrowerState);
        })
        .catch((error) => {
          alert("检查借书者ID失败！请联系管理员");
          console.log(error);
        })
        .then(() => {
          if (this.checkList.borrowerIDTrue == 1) {
            return;
          } else {
            console.log(this.checkList.borrowerIDTrue);
            this.$message({
              message: "未查询到此借阅者号！",
              type: "error",
            });
          }
        });
    },
  },
  created() {
    if (this.form.borrowerID != null) {
      console.log(this.form.borrowerID);
      this.checkBorrower(); //获取借阅证状态
    }
  },
};
</script>

<style lang="scss" scoped>
.form1 {
  width: 50%;
  margin: 0 auto; //居中
}
.btn {
  height: 36px;
  width: 45%;
  margin: 0 auto; //居中
  margin-top: 15px;
  margin-left: 30%;
  margin-right: 50%;
}
.tableTitle {
  position: relative;
  margin: 0 auto;
  width: 600px;
  height: 1px;
  background-color: #d4d4d4;
  text-align: center;
  font-size: 16px;
  color: rgba(101, 101, 101, 1);
}
.midText {
  position: absolute;
  left: 50%;
  background-color: #ffffff;
  padding: 0 15px;
  transform: translateX(-50%) translateY(-50%);
}
.tips {
  font-size: 20px;
  line-height: 30px;
  font-weight: 800;
  color: #ce0b3f;
  text-align: center;
}
.formSize {
  width: 50%;
}
</style>
