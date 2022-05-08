<template>
  <div class="container">
    <div class="tableTitle"><span class="midText">个人信息修改</span></div>
    <br />
    <div>
      <el-form class="form1" :model="param" label-width="80px">
        <el-form-item prop="account">
          <el-input v-model="param.account" placeholder="账号">
            <template #prepend>
              <el-button>账号</el-button>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="username">
          <el-input v-model="param.username" placeholder="用户名">
            <template #prepend>
              <el-button>用户名</el-button>
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
              <el-button icon="el-icon-phone-outline">电话</el-button>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="username">
          <el-input v-model="param.user_email" placeholder="邮箱">
            <template #prepend>
              <el-button icon="el-icon-bell">邮箱</el-button>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="username">
          <el-input v-model="param.user_id_number" placeholder="身份证号">
            <template #prepend>
              <el-button icon="el-icon-user-solid">身份证号</el-button>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            type="password"
            placeholder="修改信息请输入密码验证"
            v-model="param.password"
          >
            <template #prepend>
              <el-button icon="el-icon-password">请输入密码</el-button>
            </template>
          </el-input>
        </el-form-item>
      </el-form>
      <div class="login-btn">
        <el-button type="primary" @click="submitForm()">修改个人信息</el-button>
      </div>
      <p class="tips">请完善你的更多信息<br />我们竭诚为您服务</p>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      param: {
        account: "",
        username: "",
        user_sex: null,
        user_tel: null,
        user_email: null,
        user_id_number: null,
        password: null,
      },
    };
  },
  created() {
    this.getInfo();
  },

  methods: {
    submitForm() {
      //axios.defaults.headers.common['Authorization'] = AUTH_TOKEN;
      axios
        .post("http://127.0.0.1:5000" + "/modUserInfo", this.param)

        .then((Response) => {
          if (Response.data.isSuccess == true) {
            this.$message.success("修改信息成功!");
            this.param.password = null;
          } else {
            this.$message.error("修改信息失败!" + Response.data.msg);
          }
        });
    },
    getInfo() {
      //获取个人信息
      axios
        .get("http://localhost:5000/getUserInfo", {
          params: {
            account: localStorage.getItem("ms_account"),
          },
        })
        .then((Response) => {
          console.log(Response);
          this.param = Response.data;
          // this.param.user_sex = Response.data.user_sex == 1 ? "男" : "女";
        });
    },
  },
};
</script>

<style scoped>
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
.login-wrap {
  position: relative;
  width: 100%;
  height: 100%;
  background-image: url(../assets/img/scut_background.jpg);
  background-size: 100%;
}
.form1 {
  width: 50%;
  margin: 0 auto;
}
.tips {
  font-size: 20px;
  line-height: 30px;
  font-weight: 800;
  color: blue;
  text-align: center;
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
  margin: -240px 0 0 -175px;
  border-radius: 5px;
  background: rgba(255, 255, 255, 0.3);
  overflow: hidden;
}
.ms-content {
  padding: 30px 30px;
}
.midText {
  position: absolute;
  left: 50%;
  background-color: #ffffff;
  padding: 0 15px;
  transform: translateX(-50%) translateY(-50%);
}
.login-btn button {
  position: center;
  width: 20%;
  height: 36px;
  margin-bottom: 10px;
  display: block;
  margin: 0 auto;
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
