<template>
  <div>
    <div class="container">
      <div class="handle-box">
        <el-button
          type="primary"
          v-if="isShow"
          icon="el-icon-search"
          @click="getUnreturned"
          >继续查看未还书籍</el-button
        >
      </div>
    </div>

    <el-table
      :data="tableData"
      border
      class="table"
      ref="multipleTable"
      header-cell-class-name="table-header"
      @selection-change="handleSelectionChange"
    >
      <!-- 选择 -->
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

      <el-table-column label="封面图(查看大图)" align="center">
        <template #default="scope">
          <el-image
            class="table-td-link"
            :src="scope.row.link"
            :preview-src-list="[scope.row.link]"
          ></el-image>
        </template>
      </el-table-column>
      <el-table-column prop="borrow_time" label="借阅时间"> </el-table-column>
      <el-table-column prop="should_return" label="应还书时间">
      </el-table-column>

      <el-table-column prop="return_time" label="还书时间"> </el-table-column>

      <el-table-column sortable prop="borrow_state" label="状态" align="center">
        <template #default="scope">
          <el-tag
            :type="
              scope.row.borrow_state === '已归还'
                ? 'success'
                : scope.row.borrow_state === '图书逾期'
                ? 'danger'
                : scope.row.borrow_state === '借出'
                ? 'warning'
                : ''
            "
            >{{ scope.row.borrow_state }}</el-tag
          >
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" align="center">
        <template #default="scope">
          <el-button
            v-if="scope.row.borrow_state === '借出'"
            type="text"
            class="green"
            icon="el-icon-edit"
            @click="handleReturn(scope.$index, scope.row)"
            >还书</el-button
          >
          <el-button
            v-if="scope.row.borrow_state === '图书逾期'"
            type="text"
            class="red"
            icon="el-icon-edit"
            @click="handleOvertimeReturn(scope.$index, scope.row)"
            >还书</el-button
          >
          <el-button
            v-if="scope.row.borrow_state === '已归还'"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.$index, scope.row)"
            >删除记录</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <!-- 还书弹出框 -->
    <el-dialog title="还书 请检查信息！" v-model="returnVisible" width="30%">
      <el-form ref="form" :model="form" label-width="70px">
        <!-- form是在点按钮的时候自动把这一行的信息存到form中 -->
        <el-form-item label="书籍id">
          <el-input :disabled="true" v-model="form.book_id"></el-input>
        </el-form-item>
        <el-form-item label="书名">
          <el-input :disabled="true" v-model="form.nameOfBook"></el-input>
        </el-form-item>
        <el-form-item label="归还日期">
          <!-- 注意这里没用form.returnTime 返回信息时要特别注意 -->
          <!-- 用form.有异步问题 没找到方法解决 -->
          <el-input :disabled="true" v-model="returnTime"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="returnVisible = false">取 消</el-button>
          <el-button type="primary" @click="returnBook">确 定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 超期还书弹出框 -->
    <el-dialog
      title="超期还书 请检查信息！"
      v-model="returnOvertimeVisible"
      width="30%"
    >
      <p class="overtime">超期还书</p>
      <el-form ref="form" :model="form" label-width="70px">
        <el-form-item label="书籍id">
          <!-- 注意这里的id是小写！！！ -->
          <el-input :disabled="true" v-model="form.id"></el-input>
        </el-form-item>
        <el-form-item label="书名">
          <el-input :disabled="true" v-model="form.bookName"></el-input>
        </el-form-item>
        <el-form-item label="归还日期">
          <!-- 注意这里没用form.returnTime 返回信息时要特别注意 -->
          <!-- 用form.有异步问题 没找到方法解决 -->
          <el-input :disabled="true" v-model="returnTime"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="returnOvertimeVisible = false">取 消</el-button>
          <el-button type="primary" @click="returnBook">确 定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 页码 -->
    <div class="pagination">
      <el-pagination
        background
        layout="total, prev, pager, next"
        :current-page="query.pageIndex"
        :page-size="query.pageSize"
        :total="pageTotal"
        @current-change="handlePageChange"
      ></el-pagination>
    </div>
    <div style="margin-top: 20px">
      <el-button @click="toggleSelection()">取消选择</el-button>
      <el-button type="primary" style="float:right" @click="returnBook1"
        >还 书</el-button
      >
      <el-button type="primary" style="float:right" @click="renew"
        >续 借</el-button
      >
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "basetable",
  data() {
    return {
      searchChoice: [
        {
          label: "全局搜索",
          options: [
            {
              value: "自动匹配",
              label: "自动匹配",
            },
          ],
        },
        {
          label: "精确搜索",
          options: [
            {
              value: "编号",
              label: "编号",
            },
            {
              value: "书名",
              label: "书名",
            },
            {
              value: "作者",
              label: "作者",
            },
            {
              value: "出版社",
              label: "出版社",
            },
            {
              value: "图书类型",
              label: "图书类型",
            },
          ],
        },
      ],

      query: {
        account: "",
        address: "",
        name: "",
        pageIndex: 1,
        pageSize: 10,
        class: "", //查询类别
        content: "", //输入的内容
      },
      returnBookMsg: {
        book_ids: [],
        account: localStorage.getItem("ms_account"),
      },
      borrow: {
        account: "",
        book_ids: [],
      },

      tableData: [],
      multipleSelection: [],
      delList: [],
      editVisible: false,
      returnVisible: false, //还书弹出框
      returnOvertimeVisible: false,
      returnTime: "",
      pageTotal: 0,

      form: {},
      idx: -1,
      id: -1,
    };
  },

  created() {
    this.query.account = localStorage.getItem("ms_account");
    this.getData();
    this.returnTime = this.getDate();
  },
  methods: {
    //查看已借书籍信息
    getData() {
      this.borrow.account = localStorage.getItem("ms_account");
      axios
        .get("http://127.0.0.1:5000" + "/borrowbooklist", {
          params: {
            account: localStorage.getItem("ms_account"),
          },
          headers: {
            token: localStorage.getItem("token"), //token换成从缓存获
          },
        })
        .then((Response) => {
          console.log("传参：" + this.borrow);
          console.log(Response.data);
          this.tableData = Response.data;
          this.resultNum = Response.data.length;
          this.loading = false;
        })
        .then(() => {
          //  setTimeout(() => {

          this.handlePageChange(this.query.pageindex);

          //  }, 1000);
        });

      console.log(this.tableData);
      this.borrow.book_ids = [];
      return;
    },
    toggleSelection(rows) {
      //取消选择
      if (rows) {
        rows.forEach((row) => {
          this.$refs.multipleTable.toggleRowSelection(row);
        });
      } else {
        this.$refs.multipleTable.clearSelection();
      }
    },

    getDate() {
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
      return todayDate;
    },
    // 触发搜索按钮
    handleSearch() {
      this.$set(this.query, "pageIndex", 1);
      this.getData();
    },
    getUnreturned() {
      this.isShow = !this.isShow;
      this.getData();
      this.$message.success("查询成功");
    },
    // 删除操作
    handleDelete(index) {
      // 二次确认删除
      this.$confirm("确定要删除吗？", "提示", {
        type: "warning",
      })
        .then(() => {
          this.$message.success("删除成功");
          this.tableData.splice(index, 1);
        })
        .catch(() => {});
    },
    // 多选操作
    handleSelectionChange(val) {
      this.multipleSelection = val;
    },

    // 编辑操作
    handleEdit(index, row) {
      this.idx = index;
      this.form = row;
      this.editVisible = true;
    },

    //每一行的还书
    handleReturn(index, row) {
      //展示还书确认
      // 注意！ 必须要toString 不然会报错，因为后端需要接收String类型的值
      this.returnBookMsg.book_ids.push(row.book_id.toString());
      // console.log(row);
      // console.log(index);
      console.log("要还的书的id:" + this.returnBookMsg.book_ids + "请检查！");

      this.idx = index;
      this.form = row;
      this.returnVisible = true;
    },
    //超期还书
    handleOvertimeReturn(index, row) {
      this.returnBookMsg.bookId = row.id;
      this.idx = index;
      this.form = row;
      this.returnOvertimeVisible = true;
    },
    //续借
    renew() {
      console.log("续借");
      // this.isShow = true;
      this.borrow.account = localStorage.getItem("ms_account");
      var i = 0;
      //把按钮所选的书籍id号码(this.multipleSelection)，推到borrow的book_ids数组中
      for (; i < this.multipleSelection.length; i++) {
        this.borrow.book_ids.push(this.multipleSelection[i].book_id.toString());
      }
      axios
        .post(" http://localhost:5000" + "/renew", this.borrow, {
          headers: {
            token: localStorage.getItem("token"), //token换成从缓存获
          },
        })
        .then((Response) => {
          let array = Response.data;
          console.log(Response.data);
          console.log(array);
          if (array !== undefined) {
            console.log("续借成功列表：" + array);
            location.reload(); //刷新页面信息
            //array是对象 不好输ID暂缓
            this.$message.success("图书" + "续借成功！");
          } else {
            console.log("续借失败");
            this.$message.error("续借失败！已超出续借次数！");
          }
          // this.tableData = Response.data;
          // this.resultNum = Response.data.length;
          this.loading = false;
        });
      // .then(() => {
      //   //  setTimeout(() => {

      //   // this.handlePageChange(this.query.pageindex);
      //   //  }, 1000);
      //   // console.log(this.tableData);
      //   return;
      // });
      this.borrow.book_ids = [];
    },

    returnBook() {
      //每一行对应的还书   由确认按钮触发
      this.$confirm("请检查信息", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(() => {
        axios
          .post("http://127.0.0.1:5000" + "/returnbooks", this.returnBookMsg)
          .then((Response) => {
            console.log("返回信息");
            console.log(Response);
            // this.returnBookMsg.true = Response.data.true;
            // console.log(this.returnBookMsg.true);
            let returnedList = Response.data.returned;
            console.log(returnedList);
            if (returnedList.length !== 0) {
              console.log("还书成功");
              location.reload(); //刷新页面信息
              this.$message.success(`归还成功`);
              // this.$set(this.tableData, this.idx, this.form);
            } else {
              this.$message.error(`还书失败`);
            }
          })
          .catch((error) => {
            alert("还书有误！请联系管理员");
            console.log(error);
            return;
          });

        this.returnBookMsg.book_ids = []; //清空选择
        this.form.returnTime = this.returnTime;
        //不做区分 全部不可见
        this.returnVisible = false;
        this.returnOvertimeVisible = false;
      });
    },

    returnBook1() {
      //总还书
      console.log("总还书");
      // this.isShow = true;
      this.borrow.account = localStorage.getItem("ms_account");
      var i = 0;
      //把按钮所选的书籍id号码(this.multipleSelection)，推到borrow的book_ids数组中
      for (; i < this.multipleSelection.length; i++) {
        this.borrow.book_ids.push(this.multipleSelection[i].book_id.toString());
      }
      console.log("还书ID：" + this.borrow.book_ids);
      axios
        .post("http://localhost:5000" + "/returnbooks", this.borrow, {
          headers: {
            token: localStorage.getItem("token"), //token换成从缓存获
          },
        })
        .then((Response) => {
          console.log(Response.data);

          if (JSON.stringify(Response.data.returned) === "[]") {
            this.tableData = Response.data.unableToReturn;
            this.$message.error("下列书籍还书失败!");
          } else {
            this.tableData = Response.data.returned;
            this.$message.success("还书成功!表中展示已还书籍");
            console.log(Response.data.returned);
            console.log(this.tableData);

            console.log("还书成功");
          }

          this.resultNum = Response.data.length;
          this.loading = false;
          //  setTimeout(() => {

          this.handlePageChange(this.query.pageindex);
          //  }, 1000);
        })
        .catch(function(error) {
          if (error.Response) this.$message.error("还书失败!");
        });
      this.borrow.book_ids = [];
    },
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
      console.log("templist如下");
      console.log(this.tempList);
    },

    // 分页导航
    handlePageChange(val) {
      this.$set(this.query, "pageIndex", val);
      this.getData();
    },
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
.green {
  color: #19920afd;
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
.overtime {
  color: #ff0000;
  height: 4em;
  line-height: 4em;
  overflow: hidden;
  font-size: large;
  text-align: center;
}
</style>
