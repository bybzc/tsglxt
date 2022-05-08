import { createRouter, createWebHistory } from "vue-router";
import Home from "../views/Home.vue";

const routes = [{
        path: '/',
        redirect: '/login'
    }, {
        path: "/",
        name: "Home",
        component: Home,
        children: [{
                path: "/dashboard",
                name: "dashboard",
                meta: {
                    title: '系统首页'
                },
                component: () =>
                    import (
                        /* webpackChunkName: "dashboard" */
                        "../views/Dashboard.vue")
            },
            {
                path: "/getBookIfo",
                name: "getBookIfo",
                meta: {
                    title: '图书检索'
                },
                component: () =>
                    import (
                        /* webpackChunkName: "table" */
                        "../views/getBookIfo.vue")
            },
            {
                path: "/myBook",
                name: "myBook",
                meta: {
                    title: '我的借阅'
                },
                component: () =>
                    import (
                        /* webpackChunkName: "table" */
                        "../views/myBook.vue")
            },
            {
                path: "/borrow",
                name: "borrow",
                meta: {
                    title: '借阅'
                },
                component: () =>
                    import (
                        /* webpackChunkName: "charts" */
                        "../views/borrowBook.vue")
            },
            {
                path: "/returnAndRenew",
                name: "returnAndRenew",
                meta: {
                    title: '查看已借书籍'
                },
                component: () =>
                    import (
                        /* webpackChunkName: "charts" */
                        "../views/returnAndRenew.vue")
            },

            {
                path: "/info",
                name: "info",
                meta: {
                    title: '个人中心'
                },
                component: () =>
                    import (
                        /* webpackChunkName: "charts" */
                        "../views/PersonalCenter.vue")
            },
            {
                path: "/myReader",
                name: "myReader",
                meta: {
                    title: '我的借阅证'
                },
                component: () =>
                    import (
                        /* webpackChunkName: "charts" */
                        "../views/myReader.vue")
            },
            // {
            //     path: "/debug",
            //     name: "debug",
            //     meta: {
            //         title: 'debug'
            //     },
            //     component: () =>
            //         import (
            //             /* webpackChunkName: "charts" */
            //             "../views/testBug.vue")
            // }, 
            //admin管理员界面
            {
                path: "/admin",
                name: "admin",
                meta: {
                    title: '管理员操作',
                    permission: true
                },
                component: () =>
                    import (

                        "../views/admin/admin.vue")
            },
            {
                path: "/announce",
                name: "announce",
                meta: {
                    title: '管理公告操作',
                    permission: true
                },
                component: () =>
                    import (

                        "../views/admin/announce.vue")
            },
            {
                path: "/gradeUp",
                name: "gradeUp",
                meta: {
                    title: '升级管理员'
                },
                component: () =>
                    import (
                        /* webpackChunkName: "login" */
                        "../views/admin/gradeUp.vue")
            },
            {
                path: "/userList",
                name: "userList",
                meta: {
                    title: '用户列表',
                    permission: true
                },
                component: () =>
                    import (

                        "../views/admin/userList.vue")
            },

            {
                path: "/borrowList",
                name: "borrowList",
                meta: {
                    title: '管理员视图',
                    permission: true
                },
                component: () =>
                    import (

                        "../views/admin/borrowList.vue")
            },
            {
                path: "/commentList",
                name: "commentList",
                meta: {
                    title: '评论列表',
                    permission: true
                },
                component: () =>
                    import (

                        "../views/admin/commentList.vue")
            },
            {
                path: "/ruleList",
                name: "ruleList",
                meta: {
                    title: '规则列表',
                    permission: true
                },
                component: () =>
                    import (

                        "../views/admin/rule.vue")
            }, {
                path: "/tabs",
                name: "tabs",
                meta: {
                    title: 'tab标签'
                },
                component: () =>
                    import (
                        /* webpackChunkName: "tabs" */
                        "../views/Tabs.vue")
            }, {
                path: "/donate",
                name: "donate",
                meta: {
                    title: '鼓励作者'
                },
                component: () =>
                    import (
                        /* webpackChunkName: "donate" */
                        "../views/Donate.vue")
            }, {
                path: "/permission",
                name: "permission",
                meta: {
                    title: '权限管理',
                    permission: true
                },
                component: () =>
                    import (
                        /* webpackChunkName: "permission" */
                        "../views/Permission.vue")
            }, {
                path: "/icon",
                name: "icon",
                meta: {
                    title: '自定义图标'
                },
                component: () =>
                    import (
                        /* webpackChunkName: "icon" */
                        "../views/Icon.vue")
            }
        ]
    }, {
        path: "/login",
        name: "Login",
        meta: {
            title: '登录'
        },
        component: () =>
            import (
                /* webpackChunkName: "login" */
                "../views/Login.vue")
    },
    {
        path: "/register",
        name: "register",
        meta: {
            title: '注册'
        },
        component: () =>
            import (
                /* webpackChunkName: "register" */
                "../views/register.vue")
    },
    {
        path: "/reserve",
        name: "reserve",
        meta: {
            title: '取消注册'
        },
        component: () =>
            import (
                /* webpackChunkName: "login" */
                "../views/reserve.vue")
    },
];

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
});

router.beforeEach((to, from, next) => {
    document.title = `${to.meta.title} | 图书管理系统`;
    const role = localStorage.getItem('account_type');
    if (!role && (to.path !== '/myReader' && to.path !== '/login' && to.path !== '/register')) {
        next('/login');
    } else if (to.meta.permission) {
        // 如果是管理员权限则可进入，这里只是简单的模拟管理员权限而已
        role === 'admin' ?
            next() :
            next('/permission');
    } else {
        next();
    }
});

export default router;