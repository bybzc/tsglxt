<template>
  <div class="container">
    <div class="tableTitle"><span class="midText">我的借阅证</span></div>
    <br />
    <div>
      <el-form class="form1" :model="form" label-width="80px">
        <el-form-item label="账号" label-width="330px">
          <el-tag> {{ form.account }}</el-tag>
        </el-form-item>
        <el-form-item label="借阅证号" label-width="330px">
          <el-tag> {{ form.reader_id }}</el-tag>
        </el-form-item>
        <el-form-item label="可借数目" label-width="330px">
          <el-tag>{{ form.borrow_booknum }}</el-tag>
        </el-form-item>

        <el-form-item label="在借数目" label-width="330px">
          <el-tag>{{ form.borrowing_num }}</el-tag>
        </el-form-item>
        <el-form-item label="借阅证等级" label-width="330px">
          <el-tag type="warning"> {{ form.grade_name }}</el-tag>
        </el-form-item>
        <el-form-item label="可借天数" label-width="330px">
          <el-tag type="info">{{ form.borrow_day }}</el-tag>
        </el-form-item>
        <el-form-item label="可续借次数" label-width="330px">
          <el-tag type="info">{{ form.renew_num }}</el-tag>
        </el-form-item>
        <el-form-item label="可续借天数" label-width="330px">
          <el-tag type="info">{{ form.renew_day }}</el-tag>
        </el-form-item>

        <el-form-item label="借阅证状态" label-width="330px">
          <div>
            <el-tag
              :type="
                form.reader_state === '正常'
                  ? 'success'
                  : form.reader_state === '禁止借书'
                  ? 'danger'
                  : form.reader_state === '未注册'
                  ? 'danger'
                  : ''
              "
              >{{ form.reader_state }}</el-tag
            >
          </div>
        </el-form-item>

        <el-form-item label="押金数" label-width="180px">
          <el-col
            ><el-button type="text disabled">{{ form.deposit_num }}</el-button>
          </el-col>

          <el-col :span="6">
            <el-input
              placeholder="请输入缴纳押金金额"
              v-model="yajinjine"
            ></el-input>
          </el-col>
          <el-button type="primary" @click="payfor">缴纳押金</el-button>
        </el-form-item>
      </el-form>
    </div>
    <el-button class="btn" type="primary" @click="registCard"
      >申请借阅证</el-button
    >
    <p class="tips">
      Tips : 账号注册后需完成<el-link href="/info">个人信息</el-link
      >的填写<br />才能办理借阅证，办证后请在本页面缴纳押金
    </p>

    <!-- 编辑弹出框 -->
    <el-dialog title="申请借阅证" v-model="cardVisble" width="80%">
      <el-form
        :rules="rules"
        :model="register"
        ref="register"
        class="form1"
        label-width="80px"
      >
        <el-form-item label="账号" prop="account">
          <el-input :disabled="true" v-model="register.account"></el-input>
        </el-form-item>
        申请借阅证后请缴纳押金，否则无法借阅图书！
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="cardVisble = false">取 消</el-button>
          <el-button type="primary" @click="registerCard">确 定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>
<script>
import axios from "axios";
export default {
  data() {
    return {
      url: "http://localhost:5000/alipay/recharge?",
      form: {
        account: "",
        reader_id: "",
        deposit_num: "", //缴纳的押金数
        borrowing_num: "", //目前正在借阅的数量
        reader_state: "未注册",
        grade_id: "", //不需要显示出来
        user_id: "", //不重要，不需要看
        grade_name: "", //对应的用户等级
        borrow_day: "", //一次借阅的天数
        borrow_booknum: "", //允许借阅的数量
        renew_num: "", //可以续借的次数
        renew_day: "", //可以续借的天数
      },
      register: {
        account: "",
        name: "",
        telephone: "",
        unit: "",
        radio: "",
      },
      returnMsg: {
        true: "",
        msg: "",
      },
      yajinjine: 50,
      cardVisble: false,

      rules: {
        // name: [{ required: true, message: "请输入账号", trigger: "blur" }],
        account: [
          {
            required: true,
            message: "系统有误 请联系管理员",
            // trigger: "blur",
            // validator: getInfo,
          },
        ],
        name: [
          {
            required: true,
            message: "请输入姓名！",
            trigger: "blur",
            // validator: checkB,
          },
        ],
        telephone: [
          {
            required: true,
            message: "请输入电话!",
            // trigger: "blur",
            // validator: checkB,
          },
        ],
        radio: [
          {
            required: true,
            message: "请选择身份!",
            // trigger: "blur",
            // validator: checkB,
          },
        ],
      },
    };
  },
  created() {
    this.form.account = localStorage.getItem("ms_account");
    this.getCard();
  },
  methods: {
    payfor() {
      console.log("pay");
      let account = localStorage.getItem("ms_account");
      window.open(
        this.url + "account=" + account + "&totalAmount=" + this.yajinjine
      );
    },
    registCard() {
      console.log("registCard!");

      if (this.form.reader_state !== "未注册") {
        alert("您已经申请过借阅证了！不能重复申请！");
        // 可以换为一个警告 提示已经注册过 看是更改信息还是误操作
        return;
      }
      this.register.account = localStorage.getItem("ms_account");
      this.cardVisble = true;

      // 之后弹出窗口 修改信息
    },
    registerCard() {
      // 提交注册表单

      axios
        .post("http://127.0.0.1:5000" + "/registerReader", this.register)
        .then((Response) => {
          console.log(Response),
            (this.returnMsg.true = Response.data.isSuccess);
          if (this.returnMsg.true === true) {
            this.$message({
              message: "注册成功",
              type: "success",
            });
            this.cardVisble = 0;
            this.getCard(); //刷新页面信息
            return;
          } else {
            this.$message({
              message: "注册失败:" + Response.data.msg,
              type: "error",
            });
            this.cardVisble = 0;

            return;
          }
        })
        .catch((error) => {
          alert("注册失败！请联系管理员");
          console.log("注册失败");
          console.log(error);
          // return;
        });
    },

    getCard() {
      axios
        .post("http://127.0.0.1:5000" + "/getReader", this.form)
        .then((Response) => {
          let gettrue = Response.data.isSuccess; //是否获取到借阅证
          if (gettrue) {
            var reader = Response.data.reader;
            console.log("reader信息");
            console.log(reader);
            (this.form.reader_id = reader.reader_id),
              (this.form.deposit_num = reader.deposit_num),
              (this.form.borrowing_num = reader.borrowing_num),
              (this.form.reader_state = reader.reader_state),
              (this.form.grade_id = reader.grade_id),
              (this.form.user_id = reader.user_id),
              (this.form.grade_name = reader.grade_name),
              (this.form.borrow_day = reader.borrow_day),
              (this.form.borrow_booknum = reader.borrow_booknum),
              (this.form.renew_num = reader.renew_num),
              (this.form.renew_day = reader.renew_day);
            localStorage.setItem("ms_borrow_id", reader.reader_id);
          } else {
            //没有注册借阅证
            this.$message({
              message: "当前账号未注册借阅证，请注册！",
              type: "warning",
            });
          }
        })
        .catch((error) => {
          alert("查询不到当前账户的借阅证！请联系管理员");
          console.log(error);
          return;
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
