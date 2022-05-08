<template>
  <div>
    <div class="container">
          <div style="text-align:center;color:blue">
        <h1>借书记录列表</h1>
      </div>
      <br>
      <div class="handle-box">
        
        <el-input
          v-model="query.content"
          placeholder="请输入书名"
          class="handle-input mr10"
        ></el-input>
        <el-button type="primary" icon="el-icon-search" @click="handleSearch()"
          >搜索</el-button
        >
      </div>

      <el-table
        :data="tempList"
        border
        class="table"
        ref="multipleTable"
        header-cell-class-name="table-header"
        @selection-change="handleSelectionChange"
      >
        
        <el-table-column
          sortable
          prop="book_id"
          label="ID"
          width="55"
          align="center"
        ></el-table-column>
        <el-table-column
          sortable
          prop="b_name"
          label="书名"
        ></el-table-column>
        <el-table-column sortable prop="b_author" label="作者"></el-table-column>
        <el-table-column sortable prop="b_press" label="出版社"></el-table-column>
        <el-table-column prop="user_name" label="用户名"> </el-table-column>
        <el-table-column prop="user_account" label="用户账户"> </el-table-column>
         <el-table-column prop="borrow_time" label="借阅时间"> </el-table-column>
        <el-table-column prop="should_return" label="应还书时间"></el-table-column>
        <el-table-column prop="b_situation" label="图书状态"> </el-table-column>
        <el-table-column sortable label="状态" prop="borrow_state" align="center">
          <template #default="scope">
           <el-tag
              :type="
                scope.row.borrow_state === '借出'
                  ? 'success'
                  : scope.row.borrow_state === '图书逾期'
                  ? 'danger'
                  : ''
              "
              >{{ scope.row.borrow_state }}</el-tag
            >
          </template>
        </el-table-column>
      </el-table>
      <!-- 页码 -->
      <div class="pagination">
        <el-pagination
          background
          layout="total, size, prev, pager, next"
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
// import { fetchBorrowListData } from "../../api/index";
import axios from 'axios';
// import { sendSearchData } from "../../api/index";
export default {
  name: "basetable",
  data() {
    return {
      query: {
        address: "",
        name: "",
        pageIndex: 1,
        pageSize: 10,
        content: "", //输入的内容
      },
      tableData: [],
      tempList : [],
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
    // 获取 easy-mock 的模拟数据
    getData() {
     axios.get("http://127.0.0.1:5000" + "/admin/allborrowedbooks").then((res)=>{
           console.log(res);
           this.tableData = res.data;
           this.resultNum = res.data.resultNum||10;
  }).then(()=>{
           this.query.pageindex=1;
          //  setTimeout(() => {
 this.handlePageChange(this.query.pageindex);
    //  }, 1000);
           
      })
    },
    //获取某行数据
      getoneData() {
        axios
        .post("http://127.0.0.1:5000" + "/search_record_bybookname", this.query)
        .then((Response) => {
          console.log(Response.data);
          this.tableData = Response.data.list;
          this.resultNum = Response.data.resultNum || 10;
        }).then(()=>{
           this.query.pageindex=1;
          //  setTimeout(() => {
 this.handlePageChange(this.query.pageindex);
    //  }, 1000);
           
      })
    },
    // 触发搜索按钮
    handleSearch() {
      this.getoneData();
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