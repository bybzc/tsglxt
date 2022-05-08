<template>
  <div>
    <div>
      <!-- 编辑弹出框 -->
      <!-- 显示公告细节 -->
      <el-dialog :title="dialogTitle" v-model="detailVisible">
        <p>{{ NoticeForm.announcement_content }}</p>
         <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailVisible = false">取 消</el-button>
        </span>
      </template>
      </el-dialog>
    </div>
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card shadow="hover" class="mgb20" style="height:252px;">
          <div class="user-info">
            <img src="../assets/img/QQtouxiang.jpg" class="user-avator" alt />
            <div class="user-info-cont">
              <div class="user-info-name">{{ name }}</div>
              <div>{{ role }}</div>
            </div>
          </div>
          <div class="user-info-list">
            登录时间：
            <span>{{ loginInfo.loginTime }}</span>
          </div>
          <div class="user-info-list">
            登录地点：
            <span>{{ loginInfo.loginPlace }}</span>
          </div>
        </el-card>

        <!-- 图书信息统计 -->
        <el-card shadow="hover" style="height:252px;">
          <template #header>
            <div class="clearfix">
              <span>图书详情</span>
            </div>
          </template>
          在馆图书
          <el-progress
            :percentage="tongjiInfo.book_in_can_read"
            color="#42b983"
          >
          </el-progress>
          借阅图书
          <el-progress
            :percentage="tongjiInfo.book_not_in"
            color="#f1e05a"
          ></el-progress>

          <!-- 超期图书
          <el-progress :percentage="0"></el-progress> -->

          暂停借阅
          <el-progress
            :percentage="tongjiInfo.can_not_read"
            color="#f56c6c"
          ></el-progress>
        </el-card>
      </el-col>

      <el-col :span="16">
        <!-- 信息栏 -->
        <el-row :gutter="20" class="mgb20">
          <el-col :span="8">
            <el-card shadow="hover" :body-style="{ padding: '0px' }">
              <div class="grid-content grid-con-1">
                <i class="el-icon-user-solid grid-con-icon"></i>
                <div class="grid-cont-right">
                  <div class="grid-num">{{ tongjiInfo.accessNum }}</div>
                  <div>用户访问量</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover" :body-style="{ padding: '0px' }">
              <div class="grid-content grid-con-2">
                <i class="el-icon-message-solid grid-con-icon"></i>
                <div class="grid-cont-right">
                  <div class="grid-num">{{ tongjiInfo.notifyNum }}</div>
                  <div>系统消息</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover" :body-style="{ padding: '0px' }">
              <div class="grid-content grid-con-3">
                <i class="el-icon-s-goods grid-con-icon"></i>
                <div class="grid-cont-right">
                  <div class="grid-num">{{ tongjiInfo.bookNum }}</div>
                  <div>图书数量</div>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <el-card shadow="hover" style="height:403px;">
          <template #header>
            <div class="clearfix">
              <span>场馆公告</span>
            </div>
          </template>
          <!-- 当el-table元素中注入data对象数组后，在el-table-column中用prop属性来对应对象中的键名即可填入数据，用label属性来定义表格的列名。可以使用width属性来定义列宽。 -->
          <!-- height 固定表头 strip斑马纹 -->
          <el-table
            :data="noticeData"
            :default-sort="{ prop: 'announcement_date', order: 'descending' }"
            height="300"
            stripe
            style="width:200%"
          >
            <el-table-column prop="announcement_date" label="日期" width="200" sortable>
            </el-table-column>
            <el-table-column prop="announcement_title" label="主题" > </el-table-column>
            <!-- <el-table-column
              prop="publisher"
              label="发布者"
              width="120"
              sortable
            > -->
            <!-- </el-table-column> -->
            <el-table-column label="操作" width="100" align="center">
              <template #default="scope">
                <el-button
                  type="text"
                  icon="el-icon-search"
                  @click="lookDetail( scope.row.announcement_title,scope.row.announcement_content)"
                  >查看详情</el-button
                >
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
    <!-- 图表行 -->

    <el-row :gutter="20">
      <el-col :span="12">
        <el-card shadow="hover">
          <div class="tableTitle"><span class="midText">留言板</span></div>
          <br />
          <el-input placeholder="请输入标题" v-model="leaveMessage.title">
          </el-input>
          <el-input
            type="textarea"
            :autosize="{ minRows: 9, maxRows: 9 }"
            placeholder="请输入内容"
            v-model="leaveMessage.content"
          >
          </el-input>
          <el-input
            v-model="leaveMessage.tel"
            placeholder="联系方式(选填)"
          ></el-input>
          <div align="center">
            <el-button type="success" @click="addLeaveMessage">提交</el-button>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <schart
            ref="line"
            class="schart"
            canvasId="line"
            :options="options2"
          ></schart>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
//引入vue-schart组件
import Schart from "vue-schart";
import axios from "axios";
export default {
  name: "dashboard",
  data() {
    return {
      loginInfo: {
        loginTime: "",
        loginPlace: "广州",
      },
      tongjiInfo: {
        //获取的统计信息
        accessNum: "", //借阅总数
        notifyNum: "", //通知总数
        bookNum: "", //图书总数
        book_in_can_read: "", //可以借出的百分比
        book_not_in: "", //已借出的
        can_not_read: "", //不能借出的
      },
      name: localStorage.getItem("ms_account"),
      borrowerCard: {
        id: "", //借阅证号
        account: localStorage.getItem("ms_account"),
        true: 0,
      },

      leaveMessage: {
        father_id: localStorage.getItem("user_id"),
        content: "",
        title: "",
        date: "",
        tel: "",
      },
      num:8,
      leaveTrue: "",
      leaveMsg: "",
      dialogTitle: "",
      detailVisible: false,
      // noticeData: [],
      NoticeForm: [{
        announcement_content:'',
      }],
      noticeData: [
        {
            
    "announcement_id": "2",
    "announcement_title": "中秋节快乐",
    "announcement_content": "祝zlgg中秋快乐！",
    "announcement_date": "2021-11-12 09:24:23",
    "announcement_publisher_id": "2"
  },
  {
    "announcement_id": "1",
    "announcement_title": "国庆快乐！",
    "announcement_content": "祝bygg国庆快乐！",
    "announcement_date": "2021-10-01 00:02:41",
    "announcement_publisher_id": "1"
  }
        //   date: "2021.3.1",
        //   message: "网页上线公告",
        //   publisher: "网站维护中心",
        // },
        // {
        //   date: "2021.4.1",
        //   message: "关于“清明节”图书馆放假的通知",
        //   publisher: "图书馆管委会",
        // },
        // {
        //   date: "2021.4.28",
        //   message: "关于“五一劳动节”图书馆放假的通知",
        //   publisher: "图书馆管委会",
        // },
        // {
        //   date: "2021.3.1",
        //   message: "网页上线公告",
        //   publisher: "网站维护中心",
        // },
        // {
        //   date: "2021.4.1",
        //   message: "关于“清明节”图书馆放假的通知",
        //   publisher: "图书馆管委会",
        // },
        // {
        //   date: "2021.4.28",
        //   message: "关于“五一劳动节”图书馆放假的通知",
        //   publisher: "图书馆管委会",
        // },
      ],

      options2: {
        type: "line",
        title: {
          text: "上一周各图书馆访问量",
        },
        xRorate: 25,
        labels: ["周一", "周二", "周三", "周四", "周五"],
        datasets: [
          {
            label: "五山校区",
            data: [402, 357, 370, 410, 320],
          },
          {
            label: "大学城校区",
            data: [390, 260, 309, 402, 416],
          },
          {
            label: "国际校区",
            data: [108, 178, 120, 235, 129],
          },
        ],
      },
    };
  },
  created() {
    let year = new Date().getFullYear(); //年
    let month = new Date().getMonth() + 1; //注意！月份是从0月开始获取的，所以要+1;
    let day = new Date().getDate(); //日

    //获取当天日期
    const todayDate =
      year +
      "-" +
      (month >= 10 ? month : "0" + month) +
      "-" +
      (day >= 10 ? day : "0" + day);

    this.loginInfo.loginTime = todayDate;
    this.leaveMessage.date = todayDate;
    this.getAnnouncement();
    this.getBorrowerId();
    this.getTongji();
  },
  mounted() {},
  components: {
    Schart,
  },
  computed: {
    role() {
      let role=localStorage.getItem("account_type");
      return role === "admin" ? "管理员" : "读者账户";
    },
  },

  methods: {
    getTongji() {
      //获取统计信息
      axios
        .get("http://127.0.0.1:5000" + "/indexmsg", {
          params: {
            account: localStorage.getItem("ms_account"),
          },
        })
        .then((Response) => {
          console.log("统计信息：");
          let tempData = Response.data;
          console.log(Response.data);
          this.tongjiInfo.accessNum = tempData.accessNum;
          this.tongjiInfo.bookNum = tempData.bookNum;
          this.tongjiInfo.book_in_can_read = tempData.book_in_can_read;
          this.tongjiInfo.book_not_in = tempData.book_not_in;
          this.tongjiInfo.can_not_read = tempData.can_not_read;
          this.tongjiInfo.notifyNum = tempData.notifyNum;

          // this.noticeData = Response.data.list;
          //this.resultNum = Response.data.resultNum || 50;
        })
        .catch((error) => {
          alert("获取信息失败！请联系管理员");
          console.log(error);
        });
    },
    // 编辑操作
    lookDetail(title,content) {
      console.log("查看详情");
      this.dialogTitle = title;
      this.NoticeForm.announcement_content=content;
      this.detailVisible = true;
      console.log(this.detailVisible);
      console.log(this.NoticeForm.detail);
    },
    changeDate() {
      const now = new Date().getTime();
      this.data.forEach((item, index) => {
        const date = new Date(now - (6 - index) * 86400000);
        item.name = `${date.getFullYear()}/${date.getMonth() +
          1}/${date.getDate()}`;
      });
    },
    getAnnouncement() {
      axios
      //  .get("http://127.0.0.1:5000" + "/announcement?num=16")
        .get("http://127.0.0.1:5000" + "/announcement",{params:{num:5}})
        .then((Response) => {
          console.log("公告看这里:");
          console.log(Response.data);
          this.noticeData = Response.data;
          //this.resultNum = Response.data.resultNum || 50;
        });
      console.log(this.noticeData);
      return;
    },
    addLeaveMessage() {
      this.leaveMessage.content =
        this.leaveMessage.content + "\n联系方式：" + this.leaveMessage.tel;
      console.log(this.leaveMessage);
      axios
        .post("http://127.0.0.1:5000" + "/message", this.leaveMessage)
        .then((Response) => {
          console.log("留言测试");
          console.log(Response);
          console.log("留言成功：" + Response.data);
          this.leaveTrue = Response.data;
          //  (this.leaveMsg = Response.data.msg);
        })
        .catch((error) => {
          alert("留言失败！请联系管理员");
          console.log(error);
        })
        .then(() => {
          if (this.leaveTrue == 1) {
            this.$message({
              message: "留言成功",
              type: "succes",
            });
            this.leaveMessage.msg = null;
            this.leaveMessage.tel = null;
          } else {
            this.$message({
              message: "留言失败！",
              type: "error",
            });
          }
        });
    },
    getBorrowerId() {
      //获取借阅证
      axios
        .post("http://127.0.0.1:5000" + "/getReader", this.borrowerCard)
        .then((Response) => {
          this.borrowerCard.true = Response.data.isSuccess; //是否获取到借阅证
          if (this.borrowerCard.true) {
            //成功获取到借阅证
            var reader = Response.data.reader;
            console.log("reader信息");
            console.log(reader);
            this.borrowerCard.id = reader.reader_id;
            console.log("记录借阅证号");
            localStorage.setItem("ms_borrow_id", reader.reader_id);
          } else {
            //没有注册借阅证
            let arr = [
              "当前账号未注册借阅证",
              '请前往"我的图书馆——个人中心"办理',
              "未办理借阅证无法使用全部功能",
            ];
            let str = arr.join("<br/>");
            this.$message({
              dangerouslyUseHTMLString: true,
              message: str,
              type: "warning",
            });
          }
        })
        .catch((error) => {
          alert("获取借阅证信息失败！请联系管理员");
          console.log("获取借阅证信息失败！");
          console.log(error);
          return;
        });
    },
  },
};
</script>

<style scoped>
.el-row {
  margin-bottom: 20px;
}

.grid-content {
  display: flex;
  align-items: center;
  height: 100px;
}

.grid-cont-right {
  flex: 1;
  text-align: center;
  font-size: 14px;
  color: #999;
}

.grid-num {
  font-size: 30px;
  font-weight: bold;
}

.grid-con-icon {
  font-size: 50px;
  width: 100px;
  height: 100px;
  text-align: center;
  line-height: 100px;
  color: #fff;
}

.grid-con-1 .grid-con-icon {
  background: rgb(45, 140, 240);
}

.grid-con-1 .grid-num {
  color: rgb(45, 140, 240);
}

.grid-con-2 .grid-con-icon {
  background: rgb(100, 213, 114);
}

.grid-con-2 .grid-num {
  color: rgb(45, 140, 240);
}

.grid-con-3 .grid-con-icon {
  background: rgb(242, 94, 67);
}

.grid-con-3 .grid-num {
  color: rgb(242, 94, 67);
}

.user-info {
  display: flex;
  align-items: center;
  padding-bottom: 20px;
  border-bottom: 2px solid #ccc;
  margin-bottom: 20px;
}

.user-avator {
  width: 120px;
  height: 120px;
  border-radius: 50%;
}

.user-info-cont {
  padding-left: 50px;
  flex: 1;
  font-size: 14px;
  color: #999;
}

.user-info-cont div:first-child {
  font-size: 30px;
  color: #222;
}

.user-info-list {
  font-size: 14px;
  color: #999;
  line-height: 25px;
}

.user-info-list span {
  margin-left: 70px;
}

.mgb20 {
  margin-bottom: 20px;
}

.todo-item {
  font-size: 14px;
}

.todo-item-del {
  text-decoration: line-through;
  color: #999;
}

.schart {
  width: 100%;
  height: 300px;
}
.midText {
  position: absolute;
  left: 25%;
  background-color: #ffffff;
  padding: 0 15px;
  transform: translateX(-50%) translateY(-50%);
}
</style>