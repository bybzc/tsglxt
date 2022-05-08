<template>
  <div>
    <div class="container_center">
      <div class="handle-box"></div>
      <div style="text-align:center;color:blue">
        <h1>晋升管理员</h1>
      </div>
      <br />
      <div class="inlinebox">
        <div class="left_ball">
          <img src="../../assets/img/123.jpg" />
          <!-- <img src="usrPic" /> -->
        </div>
        <div>
          <el-form
            :label-position="labelPosition"
            label-width="80px"
            :model="formLabelAlign"
          >
            <el-form-item label="账户">
              <el-input v-model="formLabelAlign.account"></el-input>
            </el-form-item> </el-form
          ><el-button type="primary" @click="commitLevel">提 交</el-button>
        </div>
        <div style="text-align:center;color:green">
          <h6>（信息完整的账户才有资格被晋升为管理员）</h6>
        </div>
        <div class="right_ball">
          <img src="../../assets/img/123.jpg" />
          <!-- <img src="usrPic" /> -->
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";
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
        limit_day: "",
        description: "",
        pageIndex: 1,
        pageSize: 10,
        // content: "", //输入的内容
      },
      labelPosition: "right",
      formLabelAlign: {
        account: "",
      },
      //  paginationShow:true,
    };
  },
  //   created() {
  //     this.getData();
  //    },
  mounted() {
    this.query.pageindex = 1;
  },
  methods: {
    commitLevel() {
      axios
        .post("http://127.0.0.1:5000/admin/setAdmin", this.formLabelAlign)
        .then((res) => {
          console.log(res);
          this.formLabelAlign.account = "";
          if (res.data.isSuccess === false) {
            this.$message({
              message: "失败：" + res.data.msg,
              type: "error",
            });
          } else {
            this.$message({
              message: "提升管理员成功!",
              type: "success",
            });
          }
        });
    },
    // 触发搜索按钮
    // handleSearch(content) {
    //   console.log(content);
    //   this.getoneData(content);
    // },

    //分页方法
    changePage(list, pageindex) {
      let from = (pageindex - 1) * this.query.pageSize;
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
      console.log(this.idx, row),
        // this.form = row;
        (this.editVisible = true);
    },
    // 保存编辑
    saveEdit() {
      this.editVisible = false;
      this.$message.success(`修改第 ${this.idx + 1} 行成功`);
      console.log(this.form);
      axios({
        methods: "post",
        url: "http://127.0.0.1:5000" + "/repair",
        data: this.form,
      }).then((result) => {
        console.log(result);
      });
      this.form = {};
    },

    // 分页导航
    handlePageChange(val) {
      this.query.pageindex = val;
      this.changePage(this.tableData, val);
    },
  },
};
</script>

<style scoped>
.inlinebox {
  display: inline-block;
  width: 80%;
}
.left_ball {
  float: left;
}
.right_ball {
  float: right;
}
.container_center {
  padding: 30px;
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 5px;
  text-align: center;
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
  width: 90%;
  font-size: 14px;
  text-align: center;
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
