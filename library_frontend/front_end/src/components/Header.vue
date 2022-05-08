<template>
  <div class="header">
    <!-- 折叠按钮 -->
    <div class="collapse-btn" @click="collapseChage">
      <i v-if="!collapse" class="el-icon-s-fold"></i>
      <i v-else class="el-icon-s-unfold"></i>
    </div>
    <div class="logo" >图书系统</div>
    <div class="header-right">
      <div class="header-user-con">
        <!-- 消息中心 -->
        <div class="btn-bell">
          <el-tooltip
            effect="dark"
            :content=" message ? `有${message}条未读消息` : `消息中心`"
            placement="bottom"
          >
            <router-link @click="messageVisible = false" to="/tabs">
              <i  class="el-icon-bell"></i>
            </router-link>
          </el-tooltip>
          <!-- 小红点 messageVisible-->
          <span class="btn-bell-badge" v-if="messageVisible"></span>
        </div>
        <!-- 用户头像 -->
        <div class="user-avator">
          <img src="../assets/img/QQtouxiang.jpg" />
          <!-- <img src="usrPic" /> -->
        </div>
        <!-- 用户名下拉菜单 -->
        <el-dropdown class="user-name" trigger="click" @command="handleCommand">
          <span class="el-dropdown-link">
            {{ username }}
            <i class="el-icon-caret-bottom"></i>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item>我的图书馆</el-dropdown-item>

              <el-dropdown-item divided command="loginout"
                >退出登录</el-dropdown-item
              >
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </div>
</template>
<script>
// import usrPic from "../assets/img/img.jpg";
export default {
  data() {
    return {
       websock: null,
       messageVisible:false,
      wsData: {
        messageCount: 0,
      },
      fullscreen: false,
      name: "admin",
      message: 0,
    };
  },
  computed: {
    username() {
      let username = localStorage.getItem("ms_account");
      return username ? username : this.name;
    },
    collapse() {
      return this.$store.state.collapse;
    },
    // usrPic: {
    //   get: function() {
    //     return "../assets/img/img.jpg";
    //   },
    // },
    usrPic() {
      localStorage.setItem("ms_usrPic", "../assets/img/img.jpg");
      let usrPic = localStorage.getItem("ms_usrPic");
      //const usrPic = "../assets/img/img.jpg";
      console.log(usrPic);
      console.log("测试啊啊");
      return usrPic; //不return 会说neveruse
    },
  },
  created(){
this. initWebSocket();
  },
  methods: {
    //测试变量
//     TEST(){
// console.log(this.messageVisible);
// console.log(this.wsData.messageCount);
//     },
    //router中点击事件可触发按钮，未使用
    messageClick(){
      console.log("点击tab:");
this.messageVisible=false;
this.wsData.messageCount=0;
console.log(this.wsData.messageCount);
    },
  initWebSocket() {
    if(typeof(WebSocket) === "undefined"){
                alert("抱歉，您的浏览器不支持socket。")
            }else{
      // 初始化weosocket
      const role = localStorage.getItem('ms_account');
      console.log("看值的：")
      console.log(role);
let url = "ws://127.0.0.1:5000/websocket/"+role;
     console.log(url);
      this.websock = new WebSocket(url);
      this.websock.onmessage = this.websocketonmessage;
      this.websock.onopen = this.websocketonopen;
      this.websock.onerror = this.websocketonerror;
      this.websock.onclose = this.websocketclose;
      }
    },
    websocketonopen() {
      // 连接建立之后执行send方法发送数据
      const role = localStorage.getItem('ms_account');
      let actions = { username: role };
      console.log(actions);
      this.websocketsend(JSON.stringify(actions));
    },
    websocketonerror() {
      // 连接建立失败重连
      //使用心跳检测来设置延时，在延时中发起重连
      // this.initWebSocket();
       console.log("重传未设置！");
    },
    websocketonmessage(data) {
      // 数据接收
      const wsDataJson = JSON.parse(data.data);
      console.log(data);
      console.log(data.data);
      this.wsData = wsDataJson;
      console.log(this.wsData.messageCount);
      console.log(this.wsData.messageCount);
      if(this.wsData.messageCount>0)
      {
        this.messageVisible=true;
        this.message=this.wsData.messageCount;
      }
    },
    websocketsend(Data) {
      // 数据发送，可用于心跳检测阶段
      this.websock.send(Data);
    },
    websocketclose(e) {
      // 关闭
      console.log("断开连接", e);
    },

    // 用户名下拉菜单选择事件
    handleCommand(command) {
      if (command == "loginout") {
        localStorage.removeItem("ms_account");
        this.$router.push("/login");
      }
    },
    // 侧边栏折叠
    collapseChage() {
      this.$store.commit("hadndleCollapse", !this.collapse);
    },
  },
  mounted() {
    if (document.body.clientWidth < 1500) {
      this.collapseChage();
    }
  },

};
</script>
<style scoped>
.header {
  position: relative;
  box-sizing: border-box;
  width: 100%;
  height: 70px;
  font-size: 22px;
  color: #fff;
}
.collapse-btn {
  float: left;
  padding: 0 21px;
  cursor: pointer;
  line-height: 70px;
}
.header .logo {
  float: left;
  width: 250px;
  line-height: 70px;
}
.header-right {
  float: right;
  padding-right: 50px;
}
.header-user-con {
  display: flex;
  height: 70px;
  align-items: center;
}
.btn-fullscreen {
  transform: rotate(45deg);
  margin-right: 5px;
  font-size: 24px;
}
.btn-bell,
.btn-fullscreen {
  position: relative;
  width: 30px;
  height: 30px;
  text-align: center;
  border-radius: 15px;
  cursor: pointer;
}
.btn-bell-badge {
  position: absolute;
  right: 0;
  top: -2px;
  width: 8px;
  height: 8px;
  border-radius: 4px;
  background: #f56c6c;
  color: #fff;
}
.btn-bell .el-icon-bell {
  color: #fff;
}
.user-name {
  margin-left: 10px;
}
.user-avator {
  margin-left: 20px;
}
.user-avator img {
  display: block;
  width: 40px;
  height: 40px;
  border-radius: 50%;
}
.el-dropdown-link {
  color: #fff;
  cursor: pointer;
}
.el-dropdown-menu__item {
  text-align: center;
}
</style>
