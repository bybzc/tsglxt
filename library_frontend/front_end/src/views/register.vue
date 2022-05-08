<template>
<!--done -->
  <div class="login-wrap">
    <div class="login">
      <div class="title">读者账号注册</div>
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
        <div class="required" v-if="!add_more">
        <el-form-item prop="account">
          <el-input v-model="param.account" placeholder="账号">
            <template #prepend>
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

        <el-form-item prop="password">
          <el-input
            type="password"
            placeholder="请再次输入密码"
            v-model="param.password2"
          >
            <template #prepend>
              <el-button icon="el-icon-lock"></el-button>
            </template>
          </el-input>
         
        </el-form-item>

        
        </div>
        <!--可选填信息-->
        <div class="optional" v-if="add_more">
        <el-form-item prop="username">
          <el-input v-model="param.username" placeholder="用户名">
            <template #prepend>
              <el-button icon="el-icon-user"></el-button>
            </template>
          </el-input>
        </el-form-item>
        
        
        <el-form-item prop="user_sex">
          <el-select
            v-model="param.user_sex"
            placeholder="性别"
            style="width:100%"
          >
            <el-option value="1" label="男"></el-option>
            <el-option value="2" label="女"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item prop="user_tel">
          <el-input v-model="param.user_tel" placeholder="电话号码">
            <template #prepend>
              <el-button icon="el-icon-phone-outline"></el-button>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="username">
          <el-input v-model="param.user_email" placeholder="邮箱">
            <template #prepend>
              <el-button icon="el-icon-bell"></el-button>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="username">
          <el-input v-model="param.user_id_number" placeholder="用户id">
            <template #prepend>
              <el-button icon="el-icon-user-solid"></el-button>
            </template>
          </el-input>
        </el-form-item>
        </div>

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







        <div class="login-btn">
          <el-button type="primary" @click="submitForm()">注册</el-button>
        </div>
          <p v-if="add_more">
          需要快速注册？点击： 
              <el-button icon="el-icon-s-tools" @click="add_more_msg"></el-button>
           
        </p>
        <p v-if="!add_more">
          想要填写更多信息？点击： 
              <el-button icon="el-icon-setting" @click="add_more_msg"></el-button>
           
        </p>
        <p>
          已有账号？马上去<span class="toRegin" @click="toLogin">登陆</span>
        </p>
      </el-form>
    </div>
  </div>
</template>

<script>
import axios from "axios";
// src="https://cdn.staticfile.org/axios/0.18.0/axios.min.js";
import SIdentify from "../components/sidentify";

export default {
  components: { SIdentify },
  data() {
    return {
      identify: {
        identifyCode: "",
        identifyCodes: "1234567890",
        code: "", //输入的验证码
      },
      add_more:false,
      param: {
        account: "",
        password: "",
        password2: "",
        username:"",
        user_sex:null,
        user_tel:null,
        user_email:null,
        user_id_number:null,
        
      },

      rules: {
        account: [{ required: true, message: "请输入账号", trigger: "blur" }],
        password: [{ required: true, message: "请输入密码", trigger: "blur" }],
        password2: [
          { required: true, message: "请再次输入密码", trigger: "blur" },
        ],
        class: [{ required: true, message: "请选择账户类型", trigger: "blur" }],
      },
      checkPass: {
        //验证密码
        isSucceed: "",
        
      },
    };
  },
  created() {
    this.$store.commit("clearTags");
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
          //进行账户密码匹配  目前是固定的
          if (this.identify.code !== this.identify.identifyCode) {
            this.$message({
              message: "验证码错误！",
              type: "error",
            });
            return;
          } else {
            if (this.param.password !== this.param.password2) {
              this.$message({
                message: "两次密码不一致！",
                type: "error",
              });
              return;
            }
          }
          //axios.defaults.headers.common['Authorization'] = AUTH_TOKEN;
          axios
            .post("http://127.0.0.1:5000" + "/register", this.param)

            .then(
              (Response) => (
                this.refreshCheck(),
                (this.checkPass.isSucceed = Response.data.isSucceed)
                
              )
            )
            .then(() => {
              if (this.checkPass.isSucceed) {
                console.log("注册成功");
                this.$message({
                  message: "注册成功，页面将于3秒后跳转",
                  type: "success",
                });
                localStorage.setItem("ms_account", this.param.account);

                setTimeout(() => {
                  this.$router.push("/login");
                }, 3000);
              } else {
                console.log("账号已存在");

                this.$message({
                  
                  message: '注册不成功' + " ，请联系客服：QQ10000",
                  //message:this.checkPass.true,
                  type: "error",
                });
              }
            });
        } else {
          this.$message.error("请填写全部信息！");
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
      this.checkPass.isSucceed = "";
      
    },

    toLogin() {
      this.$router.push("/login");
    },
    add_more_msg(){
      this.add_more=!this.add_more;

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
  text-align:center;
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
  margin: -240px 0 0 -175px;
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
