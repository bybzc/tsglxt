<template>
    <div class="">
        <div class="crumbs">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item><i class="el-icon-lx-copy"></i> tab选项卡</el-breadcrumb-item>
            </el-breadcrumb>
        </div>
        <div class="container">
            <el-tabs v-model="message">
                  <el-tab-pane :label="`回复消息(${contextTable.length})`" name="first">
                    <el-table :data="contextTable" :show-header="false" style="width: 100%">
                        <el-table-column>
                            <template #default="scope">
                                <span @click="showContent(scope.row.content)" class="message-title">{{scope.row.title}}</span>
                            </template>
                        </el-table-column>
                        <el-table-column prop="content" width="180"></el-table-column>
                        <el-table-column prop="date" width="180"></el-table-column>
                        <el-table-column width="120">
                            <template #default="scope">
                                <el-button size="small" @click="handleRead(scope.$index)">标为已读</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                    <!-- <div class="handle-row">
                        <el-button type="primary" @click="handleReadAllFeedback()">全部标为已读</el-button>
                    </div> -->
                </el-tab-pane>         
                <!-- <el-tab-pane :label="`回复消息(${contextTable.feedback.length})`" name="first">
                    <el-table :data="contextTable.feedback" :show-header="false" style="width: 100%">
                        <el-table-column>
                            <template #default="scope">
                                <span class="message-title">{{scope.row.title}}</span>
                            </template>
                        </el-table-column>
                        <el-table-column prop="date" width="180"></el-table-column>
                        <el-table-column width="120">
                            <template #default="scope">
                                <el-button size="small" @click="handleRead(scope.$index)">标为已读</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                    <div class="handle-row">
                        <el-button type="primary" @click="handleReadAllFeedback()">全部标为已读</el-button>
                    </div>
                </el-tab-pane>         

                <el-tab-pane :label="`系统公告(${contextTable.systemMsg.length})`" name="second">
                    <template v-if="message === 'second'">
                        <el-table :data="contextTable.systemMsg" :show-header="false" style="width: 100%">
                            <el-table-column>
                                <template #default="scope">
                                    <span class="message-title">{{scope.row.title}}</span>
                                </template>
                            </el-table-column>
                            <el-table-column prop="date" width="150"></el-table-column>
                            <el-table-column width="120">
                                <template #default="scope">
                                    <el-button size="small" @click="handleReadSystem(scope.$index)">标为已读</el-button>
                                </template>
                            </el-table-column>
                        </el-table>
                        <div class="handle-row">
                            <el-button type="primary" @click="handleReadAllSystem()">全部标为已读</el-button>
                        </div>
                    </template>
                </el-tab-pane>

                <el-tab-pane :label="`已读信息(${contextTable.recycle.length})`" name="third">
                    <template v-if="message === 'third'">
                        <el-table :data="contextTable.recycle" :show-header="false" style="width: 100%">
                            <el-table-column>
                                <template #default="scope">
                                    <span class="message-title">{{scope.row.title}}</span>
                                </template>
                            </el-table-column>
                            <el-table-column prop="date" width="150"></el-table-column>
                            <el-table-column width="120">
                                <template #default="scope">
                                    <el-button type="danger" @click="handleDel(scope.$index)">删除</el-button>
                                </template>
                            </el-table-column>
                        </el-table>
                        <div class="handle-row">
                            <el-button type="danger" @click="handleDelAll()">删除全部</el-button>
                        </div>
                    </template>
                </el-tab-pane> -->
   <el-dialog title="详情" v-model="contentVisible" width="30%">
      <el-card class="box-card">
  <div :v-model="contentCur" class="text item">
    {{'列表内容: ' + contentCur }}
  </div>
</el-card>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="contentClose">取 消</el-button>
        </span>
      </template>
    </el-dialog>
            </el-tabs>
        </div>
    </div>
</template>

<script>
import axios from 'axios';
    export default {
        name: 'tabs',
        data() {
            return {
                websock: null,
                contentCur:"",
                contentVisible:false,
      wsData: {
        blocksCount: 0,
        transactionsCount: 0,
        accountsCount: 0,
        evidenceCount: 0
      },
                message: 'first',
                showHeader: false,
                // contextTable:{
                // feedback: [{
                //     date: '2018-04-19 20:00:00',
                //     title: '【系统通知】该系统将于今晚凌晨2点到5点进行升级维护',
                // },{
                //     date: '2018-04-19 21:00:00',
                //     title: '今晚12点整发大红包，先到先得',
                // }],
                // systemMsg: [{
                //     date: '2018-04-19 20:00:00',
                //     title: '【系统通知】该系统将于今晚凌晨2点到5点进行升级维护'
                // }],
                // recycle: [{
                //     date: '2018-04-19 20:00:00',
                //     title: '【系统通知】该系统将于今晚凌晨2点到5点进行升级维护'
                // }], },
                 contextTable:[
                {
                    date: '2018-04-19 20:00:00',
                    title: '【系统通知】该系统将于今晚凌晨2点到5点进行升级维护',
                    content:'2342可以使用'
                },
                {
                    date: '2018-04-19 21:00:00',
                    title: '今晚12点整发大红包，先到先得',
                     content:'好的公告表中数据是否有必要每个用户都存一张表对公告中的数据操作可以为未读，已读；删除功能是否应该去除。或者公告不分未读已读zhishi使用websocket进行实时信息交互 Please enter a commit message to explain why this merge is necessary, # especially if it merges an'
                },
                {
                    date: '2018-04-19 20:00:00',
                    title: '【系统通知】该系统将于今晚凌晨2点到5点进行升级维护',
                     content:'2342你想怎么说'
                }
                ],
                tableData:[],
                account:'',
                query:{
        address: "",
        name: "",
        pageIndex: 1,
        pageSize: 10,
        class: "", //查询类别
        content: "", //输入的内容
                },
            };
            
        },
        created(){
            this.getdata();
        },
        methods: {
showContent(content){
this.contentCur=content;
this.contentVisible = true;
},
contentClose(){
this.contentVisible = false;
},
//获取数据
            getdata(){
                this.account = localStorage.getItem("ms_account");
                console.log(this.account);
 axios.get("http://127.0.0.1:5000/getnotify" ,{params:{account: localStorage.getItem("ms_account")}}).then((res)=>{
     console.log("看值：");
           console.log(res);
        this.contextTable = res.data;
        console.log(res.data);
//公告表中数据是否有必要每个用户都存一张表
//对公告中的数据操作可以为未读，已读；删除功能是否应该去除。
//或者公告不分未读已读zhi'shi
//使用websocket进行实时信息交互
  })
            },
            handleRead(index) {
                // const item = this.contextTable.feedback.splice(index, 1);
                // console.log(item);
                // this.contextTable.recycle = item.concat(this.contextTable.recycle);
                const item = this.contextTable.splice(index, 1);
                console.log(item);
            },
            handleDel(index) {
                // const item = this.read.splice(index, 1);
                // this.recycle = item.concat(this.recycle);
                 const item = this.contextTable.recycle.splice(index, 1);
                 console.log(item); 

            },
            handleReadSystem(index) {
                // //假删除此信息
                // const item = this.recycle.splice(index, 1);
                // //相当于此信息被复制到了未读的尾部
                // this.read = item.concat(this.read);
                const item = this.contextTable.systemMsg.splice(index, 1); 
                console.log(item);
                this.contextTable.recycle = item.concat(this.contextTable.recycle);
                // this.recycle = item.concat(this.recycle);
            },
           handleReadAllFeedback(){
console.log("发送请求让后端将用户对应的表格清空！");
this.getdata();

           },
            handleReadAllSystem(){
console.log("发送请求让后端将用户对应的表格清空！");
this.getdata();

           },
            handleDelAll(){
              console.log("发送请求让后端将用户对应的表格清空！");
this.getdata();
           }
        },
        computed: {
            unreadNum(){
                return this.contextTable.systemMsg.length;
            }
        }
    }

</script>

<style>
.message-title{
    cursor: pointer;
}
.handle-row{
    margin-top: 30px;
}
  .text {
    font-size: 14px;
  }
.item {
    padding: 18px 0;
  }

  .box-card {
    padding:5px;
  }
</style>


