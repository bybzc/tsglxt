<template>
  <div>
    <div class="container">
      <div class="handle-box">
        <el-button
          type="second"
          icon="el-icon-search"
          @click="handleSearchAll()"
          >搜索所有书籍</el-button
        >
        <el-select
          v-model="query.class"
          placeholder="查询类别"
          class="handle-select mr10"
        >
          <el-option-group
            v-for="group in searchChoice"
            :key="group.label"
            :label="group.label"
          >
            <el-option
              v-for="item in group.options"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            >
            </el-option>
          </el-option-group>
        </el-select>

        <el-input
          v-model="query.content"
          placeholder="请输入内容"
          class="handle-input mr10"
        ></el-input>
        <el-button type="primary" icon="el-icon-search" @click="handleSearch"
          >搜索</el-button
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
        <el-table-column type="expand">
          <template #default="props">
            <p>摘要：{{ props.row.abstractMsg }}</p>
            <br />
            <p>价格：{{ props.row.price }}</p>
          </template>
        </el-table-column>

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
        <el-table-column
          sortable
          prop="language"
          label="语言"
        ></el-table-column>

        <!-- <el-table-column sortable prop="price" label="价格"></el-table-column> -->
        <el-table-column
          sortable
          prop="keyword"
          label="关键词"
        ></el-table-column>

        <!-- <el-table-column label="封面图(查看大图)" align="center">
          <template #default="scope">
            <el-image
              class="table-td-link"
              :src="scope.row.image"
              :preview-src-list="[scope.row.image]"
            ></el-image>
          </template>
        </el-table-column> -->

        <el-table-column sortable prop="call_number" label="分类">
        </el-table-column>
        <el-table-column sortable prop="library_name" label="所在图书馆">
        </el-table-column>

        <el-table-column prop="position" label="地点"> </el-table-column>

        <el-table-column sortable prop="state" label="是否在馆" align="center">
          <template #default="scope">
            <el-tag
              :type="
                scope.row.state === '在馆'
                  ? 'success'
                  : scope.row.state === '借出'
                  ? 'danger'
                  : ''
              "
              >{{ scope.row.state }}</el-tag
            >
          </template>
        </el-table-column>
        <el-table-column prop="situation" label="书籍状态" align="center">
          <template #default="scope">
            <el-tag
              :type="
                scope.row.situation === '完好'
                  ? 'success'
                  : scope.row.situation === '严重受损'
                  ? 'danger'
                  : scope.row.situation === '一般磨损'
                  ? 'info'
                  : ''
              "
              >{{ scope.row.situation }}</el-tag
            >
          </template>
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
        <el-button type="primary" style="float:right" @click="borrowBook"
          >借 书</el-button
        >
        <el-button
          type="text"
          style="float:right"
          @click="dialogFormVisible = true"
          >预定</el-button
        >
      </div>
    </div>

    <el-dialog title="设置预定图书时间" v-model="dialogFormVisible">
      <el-select v-model="form.year" placeholder="请选择年份">
        <el-option label="2021" value="2021"></el-option>
        <el-option label="2022" value="2022"></el-option>
      </el-select>
      <el-form :model="form">
        <el-form-item label="月份" :label-width="formLabelWidth">
          <el-input v-model="form.month" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="日期" :label-width="formLabelWidth">
          <el-input v-model="form.day" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="时" :label-width="formLabelWidth">
          <el-input v-model="form.hour" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <div class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="reserveBook">确 定</el-button>
      </div>
    </el-dialog>

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
              value: "isbn",
              label: "isbn码",
            },
            {
              value: "nameOfBook",
              label: "书名",
            },
            {
              value: "author",
              label: "作者",
            },
            {
              value: "press",
              label: "出版社",
            },
            {
              value: "call_number",
              label: "图书类型",
            },
            {
              value: "keywords",
              label: "主题词",
            },
          ],
        },
      ],

      query: {
        pageIndex: 1,
        pageSize: 10,
        class: "", //查询类别 这里动态绑定下拉框
        content: "", //输入的内容
        isbn: "",
        nameOfBook: "",
        author: "",
        press: "",
        call_number: "",
        keywords: "",
      },
      borrow: {
        account: "",
        book_ids: [],
        date: "",
      },

      //response返回的参数存在这个位置

      multipleSelection: [], //点击按钮返回的所选行

      dialogTitle: {}, //弹出位置框的标题名
      loading: true,
      tableData: [], //放置查出的书的数组
      tempList: [],

      delList: [],
      editVisible: false,
      resultNum: 0,
      dialogFormVisible: false,
      form: {
        year: "",
        month: "",
        day: "",
        hour: "",
        minute: "",
        sec: "",
      },
      idx: -1,
      id: -1,
    };
  },
  created() {
    // this.getData();
    this.handleSearchAll(); //进入页面即刷新所有书籍
  },
  mounted() {
    this.query.pageindex = 1;
  },
  methods: {
    toggleSelection(rows) {
      if (rows) {
        rows.forEach((row) => {
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
    getData() {
      console.log(this.$data.query.class);
      this.query.nameOfBook = "";
      this.query.author = "";
      this.query.press = "";
      this.query.isbn = "";
      this.query.keywords = "";
      this.query.call_number = "";
      switch (this.query.class) {
        case "nameOfBook":
          this.query.nameOfBook = this.query.content;
          break;
        case "author":
          this.query.author = this.query.content;
          break;
        case "press":
          this.query.press = this.query.content;
          break;
        case "isbn":
          this.query.isbn = this.query.content;
          break;
        case "keywords":
          this.query.keywords = this.query.content;
          break;
        case "call_number":
          this.query.call_number = this.query.content;
          break;
      }

      axios
        .post("http://localhost:5000" + "/getBookIfo", this.query, {
          headers: {
            token: localStorage.getItem("token"), //token换成从缓存获
          },
        })
        .then((Response) => {
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
      return;
    },
    handleSearchAll() {
      axios
        .get("http://localhost:5000" + "/allBookIfos", {
          headers: {
            token: localStorage.getItem("token"), //token换成从缓存获
          },
        })
        .then((Response) => {
          console.log(Response.data);
          this.tableData = Response.data;
          this.resultNum = Response.data.length;
          this.loading = false;
        })
        .then(() => {
          //  setTimeout(() => {
          this.handlePageChange(this.query.pageindex);

          this.$message.success(`所有书籍搜索完成`);
        });
    },
    // 触发搜索按钮
    handleSearch() {
      //this.$set(this.query, "pageIndex", 1); //这里有问题
      this.getData();
      this.$message.success(`搜索完成`);
    },

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
    reserveBook() {
      this.dialogFormVisible = false;
      this.borrow.account = localStorage.getItem("ms_account");
      var i = 0;
      //把按钮所选的书籍id号码(this.multipleSelection)，推到borrow的book_ids数组中
      for (; i < this.multipleSelection.length; i++) {
        this.borrow.book_ids.push(this.multipleSelection[i].book_id);
      }
      this.borrow.date =
        this.form.year +
        "-" +
        this.form.month +
        "-" +
        this.form.day +
        " " +
        this.form.hour +
        ":0:0";

      axios
        .post(" http://localhost:5000" + "/reserve", this.borrow, {
          headers: {
            token: localStorage.getItem("token"), //token换成从缓存获
          },
        })
        .then((Response) => {
          console.log(Response.data);

          this.loading = false;
          if (JSON.Stringify(Response.data.unadbleToReserve) != "{}")
            this.$message.success("预定成功");
          else {
            console.log(Response);
            this.$message.error("预定失败！");
          }
        })
        .then(() => {
          //  setTimeout(() => {
          this.handlePageChange(this.query.pageindex);
          //  }, 1000);

          console.log(this.tableData);
        });
      this.borrow.book_ids = [];
    },
    //借书
    borrowBook() {
      this.borrow.account = localStorage.getItem("ms_account");
      var i = 0;
      //把按钮所选的书籍id号码(this.multipleSelection)，推到borrow的book_ids数组中
      for (; i < this.multipleSelection.length; i++) {
        this.borrow.book_ids.push(this.multipleSelection[i].book_id);
      }
      console.log(this.borrow.book_ids);
      axios
        .post(" http://localhost:5000" + "/borrow", this.borrow, {
          headers: {
            token: localStorage.getItem("token"), //token换成从缓存获
          },
        })
        .then((Response) => {
          console.log(Response.data);
          // this.tableData = Response.data;
          // this.resultNum = Response.data.length;
          this.loading = false;
          if (Response.data.isSuccess) {
            this.$message.success("借出成功!");
          } else {
            console.log(Response);
            var reason = Response.data.reason;
            var jiechu = Response.data.已被借出的书;
            var sunhuai = Response.data.损坏的书;
            let returnmsg = "";
            if (reason != null) {
              var allow = Response.data.allowBorrowBookNum;
              var borrowing = Response.data.borrwingBookNum;

              this.$message.error(
                "超出账号借书限制！当前已借：" +
                  borrowing +
                  "允许借阅总数：" +
                  allow
              );
            } else {
              if (jiechu != null) {
                returnmsg = returnmsg + "下列书已借出" + jiechu;
              }
              if (sunhuai != null) {
                returnmsg = returnmsg + "下列书严重损坏" + sunhuai;
              }

              this.$message.error("借出失败！" + returnmsg);
            }
          }
        })
        .then(() => {
          //  setTimeout(() => {
          // this.handlePageChange(this.query.pageindex);
          //  }, 1000);

          console.log(this.tableData);
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
