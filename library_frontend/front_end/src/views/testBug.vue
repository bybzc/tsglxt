<template>
  <div class="container">
    <div class="tableTitle"><span class="midText">我的借阅证</span></div>
    <br />
    <div>
      <el-form class="form1" :model="form" label-width="80px">
        <el-form-item label="账号">
          <el-input :disabled="true" v-model="form.account"></el-input>
        </el-form-item>
        <el-form-item label="姓名">
          <el-input
            :disabled="true"
            placeholder="请填写姓名"
            v-model="form.name"
          ></el-input>
        </el-form-item>

        <el-form-item label="手机号">
          <el-input
            :disabled="true"
            placeholder="请填写手机号"
            v-model="form.telephone"
          ></el-input>
        </el-form-item>
        <el-form-item label="单位">
          <el-input :disabled="true" v-model="form.unit"></el-input>
        </el-form-item>
        <el-form-item label="已借数量">
          <el-input
            :disabled="true"
            v-model="form.count"
            placeholder="借出但未还的数量"
          ></el-input>
        </el-form-item>
        <el-form-item label="借阅者状态">
          <el-input :disabled="true" v-model="form.borrowerState"></el-input>
        </el-form-item>
      </el-form>
    </div>
    <el-button class="btn" type="primary" @click="registCard"
      >申请借阅证</el-button
    >
    <p class="tips">
      Tips : 账号注册后并未申请借阅证，请在此页面申请<br />单位填写
      学院（学生/教师） 部门（员工）
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
        borrowerID: "",
        situation: "",
        bookName: "",
        author: "",
        press: "",
        publishDate: "",
        bookState: "",
        borrowerState: "",
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
            message: "请输入书籍ID",
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
      this.$refs.borrow.validate((valid) => {
        if (valid) {
          //信息有效
          if (this.form.bookState !== "可借") {
            alert("[借书失败]书籍已被借出！");
            return;
          }
          if (this.form.borrowerState !== "允许借书") {
            alert("[借书失败]当前账号禁止借书！请联系管理员");
            return;
          }
          axios
            .post("http://127.0.0.1:5000" + "/", this.param)
            .then((Response) => {
              if (Response.data.true) {
                this.$message({
                  message: "[借书成功]成功借书！请在一个月内归还！",
                  type: "true",
                });
              } else {
                alert("[借书失败]提交请求失败，请联系管理员");
              }
            });
        }
      });
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
        .post("http://127.0.0.1:5000" + "/", this.param)
        .then((Response) => {
          (this.checkList.bookTrue = Response.data.bookTrue),
            (this.form.bookName = Response.data.adminOrNot),
            (this.form.author = Response.data.true),
            (this.form.press = Response.data.press),
            (this.form.publishDate = Response.data.publishDate),
            (this.form.bookState = Response.data.bookState);
        })
        .catch((error) => {
          alert("检查书籍ID失败！请联系管理员");
          console.log(error);
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
      if (this.form.borrowerID == "") {
        this.$message({
          message: "请输入借阅者ID",
          type: "error",
        });
        return;
      }
      axios
        .post("http://127.0.0.1:5000" + "/", this.param)
        .then((Response) => {
          (this.checkList.borrowerIDTrue = Response.data.borrowerIDTrue)(),
            (this.form.borrowerState = Response.data.borrowerState);
        })
        .catch((error) => {
          alert("检查借书者ID失败！请联系管理员");
          console.log(error);
        })
        .then(() => {
          if (this.checkList.borrowerIDTrue) {
            return;
          } else {
            console.log(this.checkPass.true);
            this.$message({
              message: "未查询到此借阅者号！",
              type: "error",
            });
          }
        });
    },
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
