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
      <el-button
        type="primary"
        icon="el-icon-plus"
        @click="addEdit"
        style="float:right"
        >增加图书</el-button
      >
      <!-- 添加弹出框 -->

      <el-dialog title="编辑" v-model="addVisible" width="30%">
        <el-form ref="aform" :model="aform" label-width="70px">
          <el-form-item label="isbn">
            <el-input v-model="aform.isbn"></el-input>
          </el-form-item>
          <el-form-item label="书名">
            <el-input v-model="aform.nameOfBook"></el-input>
          </el-form-item>
          <el-form-item label="作者">
            <el-input v-model="aform.author"></el-input>
          </el-form-item>
          <el-form-item label="出版社">
            <el-input v-model="aform.press"></el-input>
          </el-form-item>
          <el-form-item label="语言">
            <el-input v-model="aform.language"></el-input>
          </el-form-item>
          <el-form-item label="摘要信息">
            <el-input v-model="aform.abstractMsg"></el-input>
          </el-form-item>
          <el-form-item label="主题字">
            <el-input v-model="aform.keyword"></el-input>
          </el-form-item>
          <el-form-item label="价格">
            <el-input v-model="aform.price"></el-input>
          </el-form-item>
          <el-form-item label="图书分类">
            <el-input v-model="aform.call_number"></el-input>
          </el-form-item>
          <el-form-item label="位置">
            <el-input v-model="aform.position"></el-input>
          </el-form-item>
          <el-form-item label="图书馆名">
            <el-select v-model="aform.library_id" placeholder="图书馆名">
              <el-option value="1" label="华工图书馆"></el-option>
              <el-option value="2" label="广州图书馆"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="损坏情况">
            <el-select v-model="aform.situation" placeholder="损坏情况">
              <el-option value="完好" label="完好"></el-option>
              <el-option value="一般破损" label="一般破损"></el-option>
              <el-option value="严重受损" label="严重受损"></el-option>
            </el-select>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="addVisible = false">取 消</el-button>
          <el-button type="primary" @click="addBook()">确 定</el-button>
        </template>
      </el-dialog>

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
        <el-table-column
          sortable
          prop="language"
          label="语言"
        ></el-table-column>
        <el-table-column
          sortable
          prop="abstractMsg"
          label="粗略信息"
        ></el-table-column>
        <el-table-column sortable prop="price" label="价格"></el-table-column>
        <el-table-column
          sortable
          prop="keyword"
          label="出版社"
        ></el-table-column>

        <el-table-column label="封面图(查看大图)" align="center">
          <template #default="scope">
            <el-image
              class="table-td-link"
              :src="scope.row.link"
              :preview-src-list="[scope.row.link]"
            ></el-image>
          </template>
        </el-table-column>

        <el-table-column sortable prop="call_number" label="分类">
        </el-table-column>
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

        <el-button
          style="float: right"
          icon="el-icon-delete"
          class="red"
          @click="deleteBook"
          >删除图书
        </el-button>
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
        pageSize: 5,
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
      },

      //response返回的参数存在这个位置

      multipleSelection: [], //点击按钮返回的所选行

      dialogTitle: {}, //弹出位置框的标题名
      loading: true,
      tableData: [], //放置查出的书的数组
      tempList: [],

      addVisible: false,

      delList: [],
      editVisible: false,
      resultNum: 0,
      form: {},
      idx: -1,
      id: -1,
      aform: {
        isbn: "",
        nameOfBook: "",
        press: "",
        language: "",
        abstractMsg: "",
        keyword: "",
        price: "",
        call_number: "",
        library_id: "",
        position: "",
        state: "在馆",
        situation: "",
        image: "",
      },
    };
  },
  created() {
    this.handleSearchAll();
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
      //   axios
      //     .get("http://localhost:5000" + "/allBookIfos",{
      //     headers: {
      //        "token":localStorage.getItem("token"),  //token换成从缓存获
      //        }
      //     }
      //     )
      //     .then((Response) => {
      //       console.log(Response.data);
      //       this.tableData = Response.data;
      //       this.resultNum = Response.data.length ;
      //       this.loading = false;
      //     }).then(()=>{
      //       //  setTimeout(() => {
      //    this.handlePageChange(this.query.pageindex);

      //   this.$message.success(`所有书籍搜索完成`);
      // });
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
    addEdit() {
      this.addVisible = true;
    },
    //修改图书信息
    modifyBook() {},

    //删除书籍
    deleteBook() {
      this.$confirm("确定要删除吗？", "提示", {
        type: "warning",
      }).then(() => {
        this.borrow.account = localStorage.getItem("ms_account");
        var i = 0;
        //把按钮所选的书籍id号码(this.multipleSelection)，推到borrow的book_ids数组中
        for (; i < this.multipleSelection.length; i++) {
          this.borrow.book_ids.push(this.multipleSelection[i].book_id);
        }
        console.log(this.borrow.book_ids);
        axios
          .post(" http://localhost:5000" + "/admin/deleteBooks", this.borrow, {
            headers: {
              token: localStorage.getItem("token"), //token换成从缓存获
            },
          })
          .then((Response) => {
            if (Response.data.isSuccess) this.$message.success("删除成功!");
            else {
              console.log(Response);
              this.$message.error("删除失败！");
            }
          })
          .then(() => {
            //  setTimeout(() => {
            this.getData();
            //  }, 1000);
          });
        this.borrow.book_ids = [];
      });
    },
    //借书

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

    //添加操作
    addBook() {
      // this.$message.success("输出成功")
      console.log("yeseresa");

      this.addVisible = false;

      console.log(this.aform);

      axios
        .post("http://localhost:5000/" + "admin/addbook", this.aform)
        .then((result) => {
          console.log(result);
          if (result.data) this.$message.success(`添加请求已发送成功！`);
          else {
            this.$message.error(`添加操作失败！`);
          }
          this.aform = {};
        });
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
