<template>
  <div>
    <div class="container">
      <div class="handle-box">
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
        :data="tempList"
        v-loading="loading"
        border
        class="table"
        ref="multipleTable"
        header-cell-class-name="table-header"
        @selection-change="handleSelectionChange"
      >
        <el-table-column
          type="selection"
          width="55"
          align="center"
        ></el-table-column>
        <el-table-column
          sortable
          prop="id"
          label="ID"
          width="55"
          align="center"
        ></el-table-column>
        <el-table-column
          sortable
          prop="bookName"
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

        <el-table-column sortable prop="sort" label="分类"> </el-table-column>
        <el-table-column sortable prop="library" label="所在图书馆">
        </el-table-column>

        <el-table-column prop="position" label="地点"> </el-table-column>
        <el-table-column label="状态" align="center">
          <template #default="scope">
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
              value: "bookid",
              label: "编号",
            },
            {
              value: "bookname",
              label: "书名",
            },
            {
              value: "bookauthor",
              label: "作者",
            },
            {
              value: "bookpress",
              label: "出版社",
            },
            {
              value: "booksort",
              label: "图书类型",
            },
          ],
        },
      ],

      query: {
        pageIndex: 1,
        pageSize: 10, 
        class: "", //查询类别 这里动态绑定下拉框
        content: "", //输入的内容
      },
      dialogTitle: {}, //弹出位置框的标题名
      loading: true,
      tableData: [],
      tempList : [],
      multipleSelection: [],
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
    getData() {
      axios
        .post("http://127.0.0.1:5000" + "/search", this.query)
        .then((Response) => {
          console.log(Response.data);
          this.tableData = Response.data.list;
          this.resultNum = Response.data.resultNum || 10;
          this.loading = false;
        }).then(()=>{
          //  setTimeout(() => {
 this.handlePageChange(this.query.pageindex);
    //  }, 1000);
           
      });
      console.log(this.tableData);
      return;
    },
    // 触发搜索按钮
    handleSearch() {
      //this.$set(this.query, "pageIndex", 1); //这里有问题
      this.getData();
      this.$message.success(`搜索完成`);
    },
    // 多选操作
    handleSelectionChange(val) {
      this.multipleSelection = val;
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
