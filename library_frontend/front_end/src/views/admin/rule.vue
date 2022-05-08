<template>
  <div>
    <div class="container_center">

          <div style="text-align:center;color:blue">
        <h1>规则列表</h1>
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
      </div>

      <el-table
        :data="tempList"
        stripe
        class="table"
        ref="multipleTable"
      
        header-cell-class-name="table-header"
      >
        <el-table-column fixed prop="frule_id" label="罚款id"  align="center" > </el-table-column>
        <el-table-column fixed prop="frule_name" label="罚款名字"  align="center"></el-table-column>
        <el-table-column fixed prop="frule_condition" label="付款详情"  align="center"></el-table-column>
       <el-table-column fixed prop="frule_amount" label="罚款数目"  align="center"></el-table-column>
       <el-table-column fixed prop="frule_ratio" label="罚款百分比"  align="center"></el-table-column>
 <el-table-column label="操作" width="80" align="center">
          <template #default="scope">
            <el-button
              type="text"
              icon="el-icon-edit"
              @click="handleEdit(scope.$index, scope.row)"
              >编辑</el-button
            >
          </template>
        </el-table-column>
      </el-table>



 <!-- 编辑弹出框 -->
    <el-dialog title="编辑" v-model="editVisible" width="30%">
      <el-form ref="form" :model="form" label-width="70px">
       <el-form-item label="ID">
       <el-input v-model="form.ID"></el-input>
       </el-form-item>
        <el-form-item label="最大数量">
          <el-input v-model="form.number"></el-input>
        </el-form-item>
    </el-form>



      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editVisible = false">取 消</el-button>
          <el-button type="primary" @click="saveEdit">确 定</el-button>
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
// import { ruleData } from "../../api/index";
export default {
  name: "basetable",
// watch:
// {
// tableData:{
// handle(oldVal,newVal){
//   console.log(newVal);
//  console.log(oldVal);
// this.changePage(this.tableData,this.query.pageindex);
// },
// deep:true,
// Immediate:true
// }
// },

  data() {
    return {
      query: {
        rules_id: "",
       borrow_num: "",
        limit_day:"",
        description:"",
        pageIndex: 1,
        pageSize: 10,
        // content: "", //输入的内容
      },
      tableData: [],
      tempList : [{
        frule_id:1,
        frule_name:"图书逾期",
        frule_condition:"罚款图书逾期天数*每天的扣费",
        frule_amount:0.50,
        frule_ratio:0.10,
      },
      {
        frule_id:2,
        frule_name:"图书丢失",
        frule_condition:"罚款图书定金加一成人工费",
        frule_amount:0,
        frule_ratio:0.10,
      },
      {
        frule_id:3,
        frule_name:"图书损坏",
        frule_condition:"罚款图书3成金额",
        frule_amount:0,
        frule_ratio:0.30,
      },
      ],
    editVisible: false,
      resultNum: 0,
      form: {},
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
    getData() {
  //     ruleData(this.query).then((res) => {
  //       console.log(res);
  //       this.tableData = res.list;
  //  this.pageTotal = res.pageTotal || 50;
  //     })
  axios.get("http://127.0.0.1:5000" + "/search_all_rules",this.query).then((res)=>{
           console.log(res);
           this.tableData = res.data.list;
           this.resultNum = res.data.resultNum||10;
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

      // 编辑操作
    handleEdit(index, row) {
      this.idx = index;
      console.log(this.idx,row),
      // this.form = row;
      this.editVisible = true;
    },
// 保存编辑
    saveEdit() {
      this.editVisible = false;
      this.$message.success(`修改第 ${this.idx + 1} 行成功`);
       console.log(this.form)
      axios({
            methods:'post',
             url:"http://127.0.0.1:5000" + "/repair",
             data:this.form
          }).then(result=>{
          console.log(result)
          });
          this.form={}
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