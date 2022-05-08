<template>
  <div class="login-wrap">
    <div class="login">
      <div class="title">图书管理系统</div>
      <!--Form组件 单域中可以放置各种类型的表单控件，包括 Input、Select、Checkbox、Radio、Switch、DatePicker、TimePicker -->
      <!--elementUI Form 表单-->
      <el-form
        :model="param"
        :rules="rules"
        ref="login"
        label-width="0px"
        class="ms-content"
      >
        <!--el-form-itm 必须写在el-form内-->
        <el-form-item prop="account">
          <el-input v-model="param.account" placeholder="账号">
            <template #prepend>
              <!--这个地方不太懂-->
              <el-button icon="el-icon-user"></el-button>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input type="password" placeholder="密码" v-model="param.password">
            <!-- @keyup.enter="submitForm()" -->
            <template #prepend>
              <el-button icon="el-icon-lock"></el-button>
            </template>
          </el-input>
        </el-form-item>

        <!-- 验证码 -->
        <el-form-item prop="checkCode">
          <el-input
            type="checkCode"
            placeholder="请输入验证码"
            v-model="identify.code"
          >
            <template #append>
              <div class="login-code" @click="refreshCode">
                <s-identify :identifyCode="identify.identifyCode"></s-identify>
              </div>
              <!-- <el-button icon="el-icon-edit"></el-button> -->
            </template>
          </el-input>
          <span class="login-code" @click="refreshCode"> </span>
        </el-form-item>

        <!-- 验证码的组件 -->

        <div class="login-btn">
          <el-button type="primary" @click="submitForm()">登录</el-button>
        </div>
        <p>
          还没有账号？马上去<span class="toRegin" @click="toRegin">注册</span>
        </p>
        <p class="login-tips">Tips : 管理图书需使用管理员账户登陆</p>
      </el-form>
    </div>
  </div>
</template>

<script>
import axios from "axios";
// src="https://cdn.staticfile.org/axios/0.18.0/axios.min.js";
import SIdentify from "../components/sidentify";
export default {
  // 验证码组件
  components: { SIdentify },
  data() {
    return {
      identify: {
        identifyCode: "",
        identifyCodes: "1234567890",
        code: "", //输入的验证码
      },

      param: {
        account: "",
        password: "",
      },

      rules: {
        account: [{ required: true, message: "请输入账号", trigger: "blur" }],
        password: [{ required: true, message: "请输入密码", trigger: "blur" }],
      },
      checkPass: {
        //验证密码
        // isSucceed:"",
        // token: "",
        // msg: "",
        user_id: "",
        admin_id: "",
        isAdmin: "",
        isSucceed: "1",
        token: "",
        type:"user",
      },
    };
  },
  created() {
    this.$store.commit("clearTags");
    this.param.account = localStorage.getItem("ms_account");
    this.refreshCode();
  },
  mounted() {
    // 注意axios是异步 js单线程语言 因而先执行js中才会执行异步中的方法
    this.identify.identifyCode = "";
    this.makeCode(this.identify.identifyCodes, 4);
  },
  methods: {
    submitForm() {
      this.$refs.login.validate((valid) => {
        if (valid) {
          if (this.identify.code !== this.identify.identifyCode) {
            this.$message({
              message: "验证码错误！",
              type: "error",
            });
            return;
          }

          /*headers: { // 设置请求头
						token: localStorage.getItem("token")
					}*/

          axios
            .post("http://127.0.0.1:5000" + "/login", this.param)
            .then((Response) => {
              console.log("response：");
              console.log(Response);
              this.refreshCheck();
              console.log(this.checkPass.isSucceed);

              if (Response.data.isSucceed) {
                this.checkPass.user_id = Response.data.user_id;
                this.checkPass.token = Response.data;
                this.checkPass.isSucceed = true;
                if (Response.data.isAdmin) {
                  this.checkPass.admin_id = Response.data.admin_id;
                  this.checkPass.type="admin";
                }
              } else {
                // 密码错误
                console.log("cuowu");
                this.checkPass.msg = Response.data;
              }
            })
            .catch((error) => {
              alert("发送登陆请求失败！请联系管理员");
              console.log(error);
            })
            .then(() => {
              if (this.checkPass.isSucceed) {
                console.log("成功登陆");
                console.log(this.checkPass.isSucceed);
                console.log(this.checkPass.type);
                localStorage.setItem("account_type", this.checkPass.type);
                localStorage.setItem("ms_account", this.param.account);
                localStorage.setItem("token", this.checkPass.token);
                localStorage.setItem("user_id", this.checkPass.user_id);
                localStorage.setItem("admin_id", this.checkPass.admin_id);
                this.$message({
                  message: "登录成功",
                  type: "success",
                });
                this.$router.push("/dashboard");
              } else {
                console.log(this.checkPass.isSucceed);
                this.$message({
                  message: "账号密码不匹配！",
                  type: "error",
                });
              }
            });
        } else {
          this.$message.error("请输入账号和密码");
          return false;
        }
      });
    },
    //验证码
    randomNum(min, max) {
      return Math.floor(Math.random() * (max - min) + min);
    },

    refreshCode() {
      this.identify.identifyCode = "";
      this.makeCode(this.identify.identifyCodes, 4);

      // // 正式上线删掉下面 不用输验证码
      // this.identify.code = this.identify.identifyCode;
    },

    makeCode(o, l) {
      for (let i = 0; i < l; i++) {
        this.identify.identifyCode += this.identify.identifyCodes[
          this.randomNum(0, this.identify.identifyCodes.length)
        ];
      }
      console.log(this.identify.identifyCode);
    },

    refreshCheck() {
      this.checkPass.token = "";
      this.checkPass.msg = "";
      this.checkPass.isSucceed = false;
    },

    toRegin() {
      this.$router.push("/register");
    },
  },
};
</script>

<style scoped>
.login-wrap {
  position: relative;
  width: 100%;
  height: 100%;
  background-image: url(../assets/img/scut_background.jpg);
  background-size: 100%;
}
.title {
  width: 100%;
  line-height: 50px;
  text-align: center;
  font-size: 20px;
  font-weight: 200px;
  color: rgb(102, 0, 235);
  border-bottom: 1px solid #ddd;
}
.login {
  position: absolute;
  left: 50%;
  top: 50%;
  width: 350px;
  margin: -190px 0 0 -175px;
  border-radius: 5px;
  background: rgba(255, 255, 255, 0.3);
  overflow: hidden;
}
.ms-content {
  padding: 30px 30px;
}
.login-btn {
  text-align: center;
}
.login-btn button {
  width: 100%;
  height: 36px;
  margin-bottom: 10px;
}
.login-tips {
  font-size: 12px;
  line-height: 30px;
  font-weight: 800;
  color: #343134;
}
.toRegin {
  color: #c7611c;
  font-style: oblique;
  cursor: pointer;
}
.login-code {
  cursor: pointer;
}
</style>
