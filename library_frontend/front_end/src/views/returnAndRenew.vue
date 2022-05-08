<template>
  <div>
    <div class="container">
      <div class="handle-box">
        
        

        
        <el-button type="primary" v-if="isShow" icon="el-icon-search" @click="getUnreturned"
          >继续查看未还书籍</el-button
        >
      </div>
      
      

      <el-table

        ref="multipleTable"
        @selection-change="handleSelectionChange"
        :data="tempList"
        v-loading="loading"
        border
        class="table"
        
        header-cell-class-name="table-header"
        
      >
        <el-table-column
          type="selection"
          width="55"
          align="center"
        ></el-table-column>
        <el-table-column
          sortable
          prop="book_id"
          label="ID"
          width="55"
          align="center"
        ></el-table-column>
        <el-table-column
          sortable
          prop="nameOfBook"
          label="书名"
        ></el-table-column>
        <el-table-column sortable prop="author" label="作者"></el-table-column>
        <el-table-column sortable prop="press" label="出版社"></el-table-column>
        <el-table-column sortable prop="language" label="语言"></el-table-column>
        <el-table-column sortable prop="abstractMsg" label="粗略信息"></el-table-column>
         <el-table-column sortable prop="price" label="价格"></el-table-column>
         <el-table-column sortable prop="keyword" label="出版社"></el-table-column>


        <el-table-column label="封面图(查看大图)" align="center">
          <template #default="scope">
            <el-image
              class="table-td-link"
              :src="scope.row.link"
              :preview-src-list="[scope.row.link]"
            ></el-image>
          </template>
        </el-table-column>

        <el-table-column sortable prop="call_number" label="分类"> </el-table-column>
        <el-table-column sortable prop="library_id" label="所在图书馆">
        </el-table-column>

        <el-table-column prop="position" label="地点"> </el-table-column>
        <el-table-column prop="state" label="状态" align="center">
          </el-table-column>
          <el-table-column prop="situation" label="状态" align="center">
            </el-table-column>
          
          <!--<template #default="scope">
            <el-tag
              :type="
                scope.row.state === '在馆'
                  ? 'success'
                  : scope.row.state === '不在馆'
                  ? 'danger'
                  : ''
              "
              >{{ scope.row.state }}</el-tag
            >
          </template>
          
        </el-table-column>
        <el-table-column label="操作" width="180" align="center">
          <template #default="scope">
            <el-button
              type="text"
              icon="el-icon-search"
              @click="searchSame(scope.$index, scope.row)"
              >查询相同书目</el-button
            >
          </template>
        </el-table-column>
        -->
      </el-table>

      
      <!-- 页码 -->
      <div class="pagination">
        <el-pagination
          background
          layout="total,size, prev, pager, next"
          :current-page="query.pageIndex"
          :page-size="query.pageSize"
          :total="resultNum"
          @current-change="handlePageChange"
        ></el-pagination>
      </div>
      <div style="margin-top: 20px">
        <el-button @click="toggleSelection()">取消选择</el-button>
    <el-button @click="returnBook">还书</el-button>
    <el-button @click="renew">续借</el-button>
    
      </div>
      
    </div>

    <!-- 编辑弹出框 -->
    <!-- 修改弹窗名为当前行的书名 -->
    <el-dialog :title="dialogTitle" v-model="editVisible" width="80%">
      <!-- <el-table :data="tableData1" stripe style="width: 100%">
        <el-table-column prop="date" label="日期" width="180">
        </el-table-column>
        <el-table-column prop="name" label="姓名" width="180">
        </el-table-column>
        <el-table-column prop="address" label="地址"> </el-table-column>
      </el-table> -->
      <p>
        o(╥﹏╥)o o(╥﹏╥)o<br />该功能正由程序员努力上线中！<br />能否给予我们一个鼓励(〃＞皿＜)
      </p>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editVisible = false">你们好菜</el-button>
          <el-button type="primary" @click="editVisible = false"
            >大大的鼓励</el-button
          >
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
// import { fetchBookData } from "../api/index";
import axios from "axios";
export default {
  name: "basetable",
  data() {
    return {
      

      query: {
        pageIndex: 1,
        pageSize: 10, 
        
      },
      isShow:false,
      borrow:{
        account:"",
        book_ids:[],
      },
     
      //response返回的参数存在这个位置
      
      multipleSelection: [],//点击按钮返回的所选行

      dialogTitle: {}, //弹出位置框的标题名
      loading: true,
      tableData: [],//放置查出的书的数组
      
      tempList : [],
      
      delList: [],
      editVisible: false,
      resultNum: 0,
      form: {},
      idx: -1,
      id: -1,
    };
  },
  created() {
    this.getData();
  }, 
  mounted(){
     this.query.pageindex=1;  
     
   
  },
  
  methods: {
    toggleSelection(rows) {
        if (rows) {
          rows.forEach(row => {
            this.$refs.multipleTable.toggleRowSelection(row);
          });
        } else {
          this.$refs.multipleTable.clearSelection();
        }
      },
      handleSelectionChange(val) {
        this.multipleSelection = val;
        //把所选按钮的对应row选取下来，作为数组放进数据中，后可用this.multipleSelection[0].isbn发送所选书籍参数
      },
      //查看已借书籍信息
    getData() {
      this.borrow.account=localStorage.getItem("ms_account");
      axios
        .get("http://127.0.0.1:5000" + "/borrowbooklist",{
          params:this.borrow,
        headers: {
           "token":localStorage.getItem("token"),  //token换成从缓存获
           }
        }
        )
        .then((Response) => {
          console.log(Response.data);
          this.tableData = Response.data;
          this.resultNum = Response.data.length ;
          this.loading = false;
        }).then(()=>{
          //  setTimeout(() => {
            
        this.handlePageChange(this.query.pageindex);
        
    //  }, 1000);
           
      });
      

      console.log(this.tableData);
      this.borrow.book_ids=[];
      return;
    },
    getUnreturned(){
      this.isShow=!this.isShow;
      this.getData();
      this.$message.success("查询成功");
    },
    //续借
    renew(){
      this.isShow=true;
      this.borrow.account=localStorage.getItem("ms_account");
      var i=0;
      //把按钮所选的书籍id号码(this.multipleSelection)，推到borrow的book_ids数组中
      for(;i<this.multipleSelection.length;i++)
      {
      this.borrow.book_ids.push(this.multipleSelection[i].book_id);
      }
      axios
        .post(" http://localhost:5000" + "/renew",this.borrow,{
             
            headers: {
           "token":localStorage.getItem("token"),  //token换成从缓存获
           }
        }
        )
        .then((Response) => {
          console.log(Response.data);
          if(Response.data)
          {
            this.$message.success("续借成功！表中展示续借成功书籍");
          }
          else
          {this.$message.error("续借失败！");}
          this.tableData=Response.data;
          this.resultNum = Response.data.length ;
          this.loading = false;
        }).then(()=>{
          
          //  setTimeout(() => {
            
       this.handlePageChange(this.query.pageindex);
    //  }, 1000);
           console.log(this.tableData);
      return;
      });
      this.borrow.book_ids=[];
      

    },
    
    // 触发搜索按钮
    
    
    delAllSelection() {
      const length = this.multipleSelection.length;
      let str = "";
      this.delList = this.delList.concat(this.multipleSelection);
      for (let i = 0; i < length; i++) {
        str += this.multipleSelection[i].name + " ";
      }
      this.$message.error(`删除了${str}`);
      this.multipleSelection = [];
    },
    // 编辑操作
    searchSame(index, row) {
      this.idx = index;
      this.form = row;
      this.dialogTitle = this.form.bookName;
      this.editVisible = true;
    },
    // 保存编辑
    saveEdit() {
      this.editVisible = false;
      this.$message.success(`修改第 ${this.idx + 1} 行成功`);
      this.$set(this.tableData, this.idx, this.form);
    },
    //还书
    returnBook()
    {
      this.isShow=true;
      this.borrow.account=localStorage.getItem("ms_account");
      var i=0;
      //把按钮所选的书籍id号码(this.multipleSelection)，推到borrow的book_ids数组中
      for(;i<this.multipleSelection.length;i++)
      {
      this.borrow.book_ids.push(this.multipleSelection[i].book_id);
      }
      axios
        .post("http://localhost:5000" + "/returnbooks",this.borrow,{
             
            headers: {
           "token":localStorage.getItem("token"),  //token换成从缓存获
           }
        }
        ).catch(function(error) {
          if(error.Response)
          this.$message.error("还书失败!");

        })
          .then((Response) => {
          console.log(Response.data);
          if(Response.data.returned==[])
          {
            
            this.tableData = Response.data.unableToReturn;
            this.$message.error("下列书籍还书失败!");

          }
          else
          {
            this.tableData = Response.data.returned;
            this.$message.success("还书成功!表中展示已还书籍")
            console.log(Response.data.returned);
            console.log(this.tableData);
            
            console.log("还书成功");
            

          }
          
          this.resultNum = Response.data.length ;
          this.loading = false;
           //  setTimeout(() => {
             
          this.handlePageChange(this.query.pageindex);
    //  }, 1000);
           
        
      
      });
     this.borrow.book_ids=[];

    },
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
      console.log("templist如下");
      console.log(this.tempList);
    },
     // 分页导航
    handlePageChange(val) {
      this.query.pageindex=val;
      this.changePage(this.tableData,val);
    }
  },
};
</script>

<style scoped>
.handle-box {
  margin-bottom: 20px;
}

.handle-select {
  width: 120px;
}

.handle-input {
  width: 300px;
  display: inline-block;
}
.table {
  width: 100%;
  font-size: 14px;
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
</style>
