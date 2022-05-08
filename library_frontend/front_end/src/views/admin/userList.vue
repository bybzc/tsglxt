<template>

  <div class="boardstyle">
    <div class="container_center">
          <div style="text-align:center;color:blue">
        <h1>用户列表</h1>
      </div>
      <br>
      <div class="handle-box">
        <div class="left_top_btn">
        <el-input
          v-model="query.account"
          placeholder="请输入账户名"
          class="handle-input mr10"
        ></el-input>
        <el-button type="primary" icon="el-icon-search" @click="handleSearch()"
          >搜索</el-button
        > 
        <el-button type="primary" class="rightSet"  @click="showUpdate()" 
          >上传用户列表excel文件</el-button
        ></div> </div>


<div>
      <el-table
        :data="tempList"
        stripe
        class="table"
        ref="multipleTable"
      
        header-cell-class-name="table-header"
      >
         <el-table-column fixed prop="user_account" label="账号"  align="center" > </el-table-column>
         <el-table-column fixed prop="user_name" label="用户名"  align="center"></el-table-column>
        <el-table-column fixed prop="user_pwd" label="密码"  align="center"></el-table-column>
        <el-table-column fixed prop="grade_id" label="用户等级"  align="center" > </el-table-column>
         <el-table-column fixed prop="reader_state" label="账户状态"  align="center" > </el-table-column>
        <el-table-column fixed prop="user_email" label="邮箱"  align="center"></el-table-column>
        <el-table-column fixed prop="user_tel" label="联系电话"  align="center" > </el-table-column>
        <el-table-column fixed prop=" user_id_number" label="用户id"  align="center" > </el-table-column>
        <el-table-column fixed prop="user_sex" label="用户性别"  align="center" >
          <template #default="scope">
<span v-if="scope.row.user_sex==1">男</span>
<span v-if="scope.row.user_sex==2">女</span>
          </template>
           </el-table-column>
       
        <el-table-column label="操作" width="180" align="center">
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

<el-dialog title="上传文件" v-model="uploadVisible" width="30%">
        <div class="right_top_btn">
        <el-upload
        class="upload-demo"
        action=" "
        :on-change="FileUpload"
        :on-remove="handleRemove"
        :on-exceed="handleExceed"
        :limit="1"
        accept="application/vnd.ms-excel,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        multiple
        :auto-upload="false"
        :file-list="fileList">
        <el-button size="small"  type="primary" icon="el-icon-upload" 
          >上传Excel文件</el-button>
           <div  class="el-upload__tip">只能上传excel、sheet文件</div>
        </el-upload></div>
         <template #footer>
           <!-- style="position:flex;justify-content: center;" -->
        <span class="dialog-footer" >
          <el-button @click="uploadVisible = false">取 消</el-button>
          <el-button type="primary" @click="uploadToServices()">确 定</el-button>
        </span>
      </template>
</el-dialog>


       <!-- 编辑弹出框 -->
    <el-dialog title="编辑" v-model="editVisible" width="30%">
          <el-form
        class="form1"
        :model="param"
        label-width="80px">
    <el-form-item prop="account">
      <el-input v-model="param.account" placeholder="账号">
        <template #prepend>
          <el-button icon="el-icon-user"></el-button>
        </template>
      </el-input>
    </el-form-item>

    <el-form-item prop="username">
      <el-input v-model="param.username" placeholder="用户名">
        <template #prepend>
          <el-button icon="el-icon-user"></el-button>
        </template>
      </el-input>
    </el-form-item>

    <el-form-item prop="user_sex">
      <el-select v-model="param.user_sex" placeholder="性别" style="width:100%">
        <el-option value="1" label="男"></el-option>
        <el-option value="2" label="女"></el-option>
      </el-select>
    </el-form-item>

    <el-form-item prop="user_tel">
      <el-input v-model="param.user_tel" placeholder="电话号码(12位以内有效长度)">
        <template #prepend>
          <el-button icon="el-icon-phone-outline"></el-button>
        </template>
      </el-input>
    </el-form-item>

    <el-form-item prop="username">
      <el-input v-model="param.user_email" placeholder="邮箱">
        <template #prepend>
          <el-button icon="el-icon-bell"></el-button>
        </template>
      </el-input>
    </el-form-item>

    <el-form-item prop="username">
      <el-input v-model="param.user_id_number" placeholder="用户id">
        <template #prepend>
          <el-button icon="el-icon-user-solid"></el-button>
        </template>
      </el-input>
    </el-form-item>
    <el-form-item prop="password">
      <el-input
        type="password"
        placeholder="修改信息请输入密码验证"
        v-model="param.password"
      ></el-input>
    
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
      </div></div></div>
  </div>
</template>

<script>
import axios from "axios";
export default {
  name: "basetable",

  data() {
    return {
      query: {
        account: "",
        password: "",
        pageIndex: 1,
        pageSize: 10,
        content: "", //输入的内容
      },
       param: {
        account: "",
        username: "",
        user_sex: null,
        user_tel: null,
        user_email: null,
        user_id_number: null,
        password: null,
      },
      data:'',
      file:'',
      fileTemp:'',
      fileList:[],
      tableData: [],
      tempList : [],
      resJson:{},
      resultNum: 0,
      uploadVisible:false,
      editVisible: false,
      form: {},
      content1:{
        account: "",
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
    // 获取 easy-mock 的模拟数据
    getData() {
     axios.get("http://127.0.0.1:5000" + "/admin/admins",{params:{
       account:null
     }}).then((res)=>{
       console.log("用户列表在这里：");
           console.log(res);
           this.tableData = res.data;
           console.log(res.data.length);
           this.resultNum = res.data.length||10;
  })
      .then(()=>{
           this.query.pageindex=1;
            this.handlePageChange(this.query.pageindex);
      })
      // .then(()=>
      //   {
           
      //   }) 
    },
    // 触发搜索按钮
    handleSearch() {
      console.log(this.query);
      axios.post("http://127.0.0.1:5000" + "/admin/admins",{params:{
       account:localStorage.getItem("ms_account"),
     }})
      .then((res)=>{
      console.log(res);
      // axios.get("http://127.0.0.1:5000" + "/search_user",this.query).then((res)=>{
           console.log(res);
           this.tableData = res.data;
           this.resultNum = res.data.resultNum||10;
  })
      .then(()=>{
           this.query.pageindex=1;
            this.handlePageChange(this.query.pageindex);
            this.query.account= "";
      })
      // })
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
      console.log("http request!")
       console.log(this.param)
      axios
        .post("http://127.0.0.1:5000" + "/modUserInfo", this.param)
// 两个主键：电话和邮箱，一个重复就报错
        .then((Response) => {
          if (Response.data.isSuccess == true) {
            this.$message.success("修改信息成功!");
            this.param.password = null;
            this.param.account=null;
            this.param.username=null;
            this.param.user_sex=null;
        this.param.user_tel= null;
        this.param.user_email= null;
        this.getData();
          } else {
            this.$message.error("修改信息失败!" + Response.data.msg);
          }
        });
    },

    // 分页导航
    handlePageChange(val) {
      this.query.pageindex=val;
      this.changePage(this.tableData,val);
    },
//上传文件时处理方法  
        FileUpload(file, fileList){
            console.log(fileList);
            console.log(file);
            this.fileTemp = file.raw;
            if(this.fileTemp){
                if((this.fileTemp.type == 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet') 
                    || (this.fileTemp.type == 'application/vnd.ms-excel')){
                    this.batchRegister(this.fileTemp);
                } else {
                    this.$message({
                        type:'warning',
                        message:'附件格式错误，请删除后重新上传！'
                    })
                }
            } else {
                this.$message({
                    type:'warning',
                    message:'请上传附件！'
                })
            }
        },
        showUpdate(){
          this.uploadVisible=true;
        },
        //超出最大上传文件数量时的处理方法
        handleExceed(){
            this.$message({
                type:'warning',
                message:'超出最大上传文件数量的限制！'
            })
            return;
        },
        //移除文件的操作方法
        handleRemove(file,fileList){
          console.log(file,fileList);
          console.log("文件行数：",file.row)
            this.fileTemp = null;
            this.resJson=null;
            //删除列表中的对应文件对应的json
        },
        //导入文件并进行处理
        batchRegister (obj) 
        {
          //使用FileReader接口，使用其中的onload方法加载，read方法读入字节流。
   const reader = new FileReader()
   const _this = this
  //  采用流的方式进行读入文件
   reader.readAsArrayBuffer(obj)
   //
   reader.onload = function () {
     const buffer = reader.result
     const bytes = new Uint8Array(buffer)
     const length = bytes.byteLength
     let binary = ''
     for (let i = 0; i < length; i++) {
       binary += String.fromCharCode(bytes[i])
     }
      const XLSX = require('xlsx')
      const wb = XLSX.read(binary, {
        type: 'binary'
      })
      const outdata = XLSX.utils.sheet_to_json(wb.Sheets[wb.SheetNames[0]])
      this.data = [...outdata]
      const arr = []
      this.data.map(v => {
        const obj = {}
        obj.account = v.account
        obj.password = v.password 
        arr.push(obj)
      })  
      //  _this.tableData = _this.tableData.concat(arr)//数据填充入表格
       
       console.log(arr);
       console.log(JSON.stringify(arr));
      _this.resJson=arr;
      } },

      uploadToServices()
      { 
        console.log(JSON.stringify(this.resJson));
        console.log("未json化的看这里：");
        console.log(this.resJson);
         axios.post("http://127.0.0.1:5000/admin/adduser",this.resJson).then(result=>{
          console.log(result) ;
          this.uploadVisible=false;
          });
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
.upload-demo{
  width: 200px;
  height: 150px;
}


.upload-demo{
  width: 170px;
}
.handle-select {
  width: 120px;
}
.handle-box {
  margin-bottom: 25px;
}
.handle-input {
  width: 300px;
  display: inline-block;
}
.table {
  width: 80%;
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
<style>
.div{
white-space: nowrap;
}
 .el-upload{
                    width: 170px;
                    height: 160px;
                    line-height: 20px;

                }
                .el-button>span{
             text-align: center;
            font-size: 16px; 
                }
  .el-upload-list--picture-card .el-upload-list__item{
                    width: 200px;
                    height: 20px;
                    line-height: 10px;
                }
.el-upload-list--picture-card .el-upload-list__item-thumbnail{
                    width: 200px;
                    height: 20px;
                    line-height: 10px;
                }
                .handle-input mr10{
position: relative;
                }

.rightSet{
  float:right;
}
.right_top_btn{
  position:relative;
  width: 170px;
height: 200px;
}
</style>

