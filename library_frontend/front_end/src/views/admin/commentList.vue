<template>
  <div>
    <div class="container_center">
          <div style="text-align:center;color:blue">
        <h1>评论列表</h1>
      </div>
      <br>
      <div class="handle-box">
        <!-- <el-input
          v-model="query.content"
          placeholder="请输入"
          class="handle-input mr10"
        ></el-input>
        <el-button type="primary" icon="el-icon-search" @click="handleSearch(query.content)"
          >搜索</el-button
        > -->
        <!-- <el-button type="primary" icon="el-icon-plus" @click="sentAnnouncementClick"
>发布公告</el-button>
<el-button type="primary" icon="el-icon-delete" @click="sentAnnouncementDel"
>删除公告</el-button> -->
      </div>
<el-row style="float:left">
  <el-col :span="50" v-for="(o, index) in 1" :key="o" :offset="index > 0 ? 2 : 0">
    <el-card :body-style="{ padding: '0px' }">
      <img src="../../assets/img/123.jpg" class="image">
      
    </el-card>
  </el-col>
</el-row>
<el-row style="float:right">
  <el-col :span="50" v-for="(o, index) in 1" :key="o" :offset="index > 0 ? 2 : 0">
    <el-card :body-style="{ padding: '0px' }">
      <img src="../../assets/img/123.jpg" class="image">
      
    </el-card>
  </el-col>
</el-row>

      <el-table
        :data="tempList"
        stripe
        class="table"
        ref="multipleTable"
      
        header-cell-class-name="table-header"
      >
        <el-table-column fixed type="index" :index="indexFn" label="序号"  align="center" > </el-table-column>
        <el-table-column fixed prop="title" label="标题"  align="center" > </el-table-column>
        <el-table-column fixed prop="content" label="内容"  align="center" > </el-table-column>
        <el-table-column fixed prop="date" label="日期"  align="center" > </el-table-column>
        <el-table-column fixed prop="state" label="状态"  align="center" > </el-table-column>
        <el-table-column label="操作" width="180" align="center">
          <template #default="scope">
            <el-button
              type="text"
              icon="el-icon-edit"
              @click="checkMsg(scope.$index, scope.row)"
              >回复</el-button
            >
          </template>
        </el-table-column>
      </el-table>

      <!-- 删除弹出框 -->
    <el-dialog title="删除" v-model="AnnouncementDelVisible" width="30%">
      <el-form   label-width="70px">
      <el-form-item label="公告id">
       <el-input v-model="announcement_id"></el-input>
       </el-form-item>
        </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="AnnouncementDelVisible = false">取 消</el-button>
          <el-button type="primary" @click="AnnouncementDel">确 定</el-button>
        </span>
      </template>
    </el-dialog>


      <!-- 添加弹出框 -->
    <el-dialog title="公告" v-model="announVisible" width="30%">
      <el-form ref="form" :model="form" label-width="70px">
      <el-form-item label="公告标题">
       <el-input v-model="form.announcement_title"></el-input>
       </el-form-item>
        <el-form-item label="公告内容">
          <el-input v-model="form.announcement_content"></el-input>
          </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="announVisible = false">取 消</el-button>
          <el-button type="primary" @click="sentAnnouncement">确 定</el-button>
        </span>
      </template>
    </el-dialog>


      <!-- 添加弹出框 -->
    <el-dialog title="回复" v-model="msgVisible" width="30%">
      <el-form ref="aform" :model="aform" label-width="70px">
      <el-form-item label="留言id">
       <el-input v-model="aform.father_id"></el-input>
       </el-form-item>
        <el-form-item label="留言标题">
          <el-input v-model="aform.title"></el-input>
        </el-form-item>
        <el-form-item label="回复内容">
          <el-input v-model="aform.content"></el-input>
          </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="msgVisible = false">取 消</el-button>
          <el-button type="primary" @click="saveFeedback">确 定</el-button>
        </span>
      </template>
    </el-dialog>


      <!-- 页码 -->
      <div class="pagination_left">
        <el-pagination
    
          background
          layout="total, size,prev, pager, next"
          :current-page="query.pageIndex"
          :page-size="query.pageSize"
          :total="resultNum"
          @current-change="handlePageChange"
        ></el-pagination>
      </div>
    </div>
  </div>
</template>



<script>
import axios from 'axios';
// import { commentData } from "../../api/index";
export default {
  name: "basetable",


  data() {
    return {
      query: {
        id: "",
        title: "",
        content: "",
        date:"",
        state:"",
        pageIndex: 1,
        pageSize: 10,
        // content: "", //输入的内容
      },
      announcement_id: "",

      tableData: [
      ],
      tempList : [
      ],
      AnnouncementDelVisible:false,
      announVisible: false,
      msgVisible: false,
      resultNum: 0,
         form: {
        announcement_title: "",
        announcement_content:"",
        announcement_publisher_id:"",
      },
      aform: {
        title: "",
        father_id: "",
        content:"",
      },
      idx: -1,
      id: -1,
      //  paginationShow:true,
    };
  },  
  created() { 
    this.getData();
   },
  mounted(){
     this.query.pageindex=1;  
   
  },
  methods: {
    indexFn(index) {
        index = (index + 1) + (this.query.pageIndex - 1) * this.query.pageSize;
        return index;
      },
    // AnnouncementDel(){
    //   console.log(this.announcement_id);
    //  axios.get("http://127.0.0.1:5000" + "/admin/delannouncement",{params:{
    //    announcement_id:this.announcement_id}})
    //       .then(result=>{
    //       console.log(result);
    //       this.$message.success(`公告已删除成功！`);
    //       this.announcement_id=" ";
    //       this.AnnouncementDelVisible=false;
    //       }) ;
    // },
//     sentAnnouncementDel(){
//     this.AnnouncementDelVisible=true;
//     },
//     sentAnnouncementClick(){
//       // this.form.account=localStorage.getItem('ms_account');
//      this.form.announcement_publisher_id=1;
// this.announVisible=true;
//     },
    //发布公告
// sentAnnouncement(){
//     this.announVisible=false;

//  console.log(this.form)
// //  axios({
// //             methods:'post',
// //              url:"http://127.0.0.1:5000" + "/add",
// //              data:this.aform
// //           })
// // axios.post("http://127.0.0.1:5000" + "/sentAnnouncement",this.form)
// axios.post("http://127.0.0.1:5000" + "/admin/addannouncement",this.form)
//           .then(result=>{
//             console.log("添加结果：");
//           console.log(result);
//           this.$message.success(`公告已发送成功！`);
//           this.form={};
//           }) ;
// },
    // 编辑操作
    checkMsg(index, row) {
      this.idx = index;
      console.log(this.idx,row),
      // this.form = row;
      this.aform.father_id=row.id;
      this.aform.title=row.title;
      this.msgVisible = true;
    },
    //发送回复信息
    saveFeedback(){
    this.msgVisible=false;
this.$message.success(`回复已发送成功！`);
 console.log(this.aform)
//  axios({
//             methods:'post',
//              url:"http://127.0.0.1:5000" + "/add",
//              data:this.aform
//           })
axios.post("http://127.0.0.1:5000" + "/admin/reply",this.aform)
          .then(result=>{
          console.log(result);
          this.aform={};
          }) ;
    },
    // 获取 easy-mock 的模拟数据
    getData() {
  //     commentData(this.query).then((res) => {
  //       console.log(res);
  //       this.tableData = res.list;
  //  this.pageTotal = res.pageTotal || 50;
  //     })
  //     .then(()=>{
  //          this.query.pageindex=1;
  //           this.handlePageChange(this.query.pageindex);
  //     })
   axios.get("http://127.0.0.1:5000" + "/admin/messages").then((res)=>{
           console.log(res);
           this.tableData = res.data;
           this.resultNum = res.data.length||10;
  })
      .then(()=>{
           this.query.pageindex=1;
          //  setTimeout(() => {
 this.handlePageChange(this.query.pageindex);
    //  }, 1000);
      })
    },
    // 触发搜索按钮
    // handleSearch(content) {
    //   console.log(content);
    //   this.getoneData(content);
    // },



    //分页方法
    changePage(list,pageindex)
    {

      let from=(pageindex - 1) * this.query.pageSize;
      let to = pageindex * this.query.pageSize;
      this.tempList = [];
      for (; from < to; from++) {
        if (list[from]) {
          this.tempList.push(list[from]);
        }
      }
    },

    // 分页导航
    handlePageChange(val) {
      console.log("看页码：");
      console.log(val);
      this.query.pageindex=val;
      this.changePage(this.tableData,val);
    }
  },
};
</script>

<style scoped>
.container_center {
    padding: 30px;
    background: #fff;
    border: 1px solid #ddd;
    border-radius: 5px;
    text-align:center;
}

.handle-box {
  margin-bottom: 25px;
}

.handle-select {
  width: 120px;
}

.handle-input {
  width: 300px;
  display: inline-block;
}
.table {
  width: 50%;
  font-size: 14px;
  text-align:center;
  margin: auto;
}
.red {
  color: #ff0000;
}
.mr10 {
  margin-right: 10px;
}
.table-td-link {
  display: block;
  margin: auto;
  width: 40px;
  height: 40px;
}
.pagination_left {
    margin: 20px 0;
    text-align: center;
    margin-left: -6%;
}
</style>