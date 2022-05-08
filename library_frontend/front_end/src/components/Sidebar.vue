<template>
  <div class="sidebar">
    <el-menu
      class="sidebar-el-menu"
      :default-active="onRoutes"
      :collapse="collapse"
      background-color="#324157"
      text-color="#bfcbd9"
      active-text-color="#20a0ff"
      unique-opened
      router
    >
      <template v-for="item in itemshow">
        <template v-if="item.subs">
          <el-submenu :index="item.index" :key="item.index">
            <template #title>
              <i :class="item.icon"></i>
              <span>{{ item.title }}</span>
            </template>
            <template v-for="subItem in item.subs">
              <el-submenu
                v-if="subItem.subs"
                :index="subItem.index"
                :key="subItem.index"
              >
                <template #title>{{ subItem.title }}</template>
                <el-menu-item
                  v-for="(threeItem, i) in subItem.subs"
                  :key="i"
                  :index="threeItem.index"
                  ><i :class="threeItem.icon"></i>
                  {{ threeItem.title }}</el-menu-item
                >
              </el-submenu>
              <el-menu-item v-else :index="subItem.index" :key="subItem.index"
                ><i :class="subItem.icon"></i> {{ subItem.title }}</el-menu-item
              >
            </template>
          </el-submenu>
        </template>
        <template v-else>
          <el-menu-item :index="item.index" :key="item.index">
            <i :class="item.icon"></i>
            <template #title>{{ item.title }}</template>
          </el-menu-item>
        </template>
      </template>
    </el-menu>
  </div>
</template>

<script>
// import bus from "../common/bus";
export default {
  data() {
    return {
      items: [
        {
          icon: "el-icon-lx-home",
          index: "dashboard",
          title: "系统首页",
          permission: true,
        },
        {
          icon: "el-icon-search",
          index: "getBookIfo",
          title: "图书检索",
          permission: true,
        },
        {
          icon: "el-icon-edit",
          index: "only_admin",
          title: "管理员权限界面",
          permission: false,
          subs: [
            //管理员操作
            {
              icon: "el-icon-edit",
              index: "admin",
              title: "管理员操作",
            },
            //管理员查阅视图
            {
              icon: "el-icon-search",
              index: "borrowList",
              title: "借书记录列表",
            },
            //用户列表视图
            {
              icon: "el-icon-search",
              index: "userList",
              title: "用户列表",
            },

            //留言列表视图
            {
              icon: "el-icon-message",
              index: "commentList",
              title: "留言列表",
            },
            //规则列表视图
            {
              icon: "el-icon-lx-calendar",
              index: "ruleList",
              title: "规则列表",
            }, //晋升管理员视图
            {
              icon: "el-icon-edit",
              index: "gradeUp",
              title: "晋升管理员",
            },
            {
              icon: "el-icon-edit",
              index: "announce",
              title: "公告管理",
            },
          ],
        },
        {
          icon: "el-icon-tickets",
          index: "mylib",
          title: "我的图书馆",
          permission: true,
          subs: [
            {
              index: "myBook",
              icon: "el-icon-edit-outline",
              title: "我的借阅",
            },
            {
              index: "getBookIfo",
              icon: "el-icon-view",
              title: "我要借书",
            },
            {
              index: "info",
              icon: "el-icon-info",
              title: "个人中心",
            },
            {
              index: "myReader",
              icon: "el-icon-info",
              title: "我的借阅证",
            },
          ],
        },
      ],
    };
  },
  computed: {
    onRoutes() {
      return this.$route.path.replace("/", "");
    },
    collapse() {
      return this.$store.state.collapse;
    },
    itemshow() {
      return this.items.filter(function(data) {
        const role = localStorage.getItem("account_type");
        let state = false;
        if (role == "admin") {
          state = true;
        } else {
          state = false;
        }
        return data.permission === true || state === true;
      });
    },
  },
};
</script>

<style scoped>
.sidebar {
  display: block;
  position: absolute;
  left: 0;
  top: 70px;
  bottom: 0;
  overflow-y: scroll;
}
.sidebar::-webkit-scrollbar {
  width: 0;
}
.sidebar-el-menu:not(.el-menu--collapse) {
  width: 250px;
}
.sidebar > ul {
  height: 100%;
}
</style>
