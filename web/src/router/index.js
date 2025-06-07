import { createRouter, createWebHashHistory, createWebHistory } from "vue-router";

//创建路由，映射关系
const router = createRouter({
    //指定采用hash模式
    // history: createWebHashHistory(),

    //指定采用history模式
    history: createWebHistory(),

    //映射关系
    routes: [
        { path: '/', redirect: '/aiexpertdecision' },
        {
            path: '/aiexpertdecision',
            component: () => import('@/components/AIExpertDecision.vue'),
        },
        {
            path: '/expertlist',
            component: () => import('@/components/ExpertList.vue'),
        },
        {
            path: '/report', 
            component: () => import('@/components/Report.vue')
        },
        { path: '/:pathMatch(.*)', component: () => import('@/components/PathError.vue') }

        /* 
        如果上一行代码改为：
        { path: '/:pathMatch(.*)*', component: () => import('@/components/PathError.vue')}
        即添加了一个*，则在template的{{ $route.params.pathMatch }}解析为数组的形式。
        */

        /*
        { path: '/user/:id', component: user },//动态路由，地址最后加参数

        在User.vue文件中：

        在template中用{{ $route.params.id }}即可拿到url中的id
        
        在script中用：

        第一种方法：
        import {useRoute} from 'vue-router'
        const route =useRoute()
        console.log(route.params.id)即可拿到id

        第二种方法：
        import { onBeforeRouteUpdate } from 'vue-router'
        onBeforeRouteUpdate((to, from) => {
            console.log(from.params.id)
            console.log(to.params.id)
        })
        */
    ]
})

// let isAdmin = false
// if (isAdmin) {
//     //顶层路由
//     router.addRoute({
//         path: '/admin',
//         component:() => import('@/components/admin/Admin.vue')
//     })

// //添加vip页面。最终效果为当同是admin和vip时才能进入/home/vip地址
// router.addRoute('home', {   //将此处配置为home的子路由。
//     path: 'vip',    //实际路由即为/home/vip
//     component: () => import('@/components/vip/vip.vue')
// })
// }

//路由导航守卫
//进行任何的路由跳转之前，传入beforeEach中的函数都会被回调。所以可以在此处进行拦截判断登录之类的功能。
//如果用户是登录状态，一般会在localStorage中保存token
// router.beforeEach((to, from) => {
//     //效果：进入到任何页面时，都跳转到login页面
//     if (to.path !== '/login') {       //不做这个判断会因为回调函数的递归，造成死循环。
//         const token = window.localStorage.getItem('token')
//         if (!token) {
//             return '/login'
//         }
//         // return的路径必须在router已注册。
//         // 如果return为false，则取消当前导航。
//         // 不return或者return undefined，则进行默认导航。
//         // return一个路由地址：1. 可以是一个string类型的路径；2. 可以是一个对象，对象中包含path、query、params等信息。
//     }

//     //效果：进入到订单页面时，判断用户是否登录
//     const token = localStorage.getItem('token')
//     if (to.path === '/order' && !token) //先判断是不是要进入订单
//         return '/login'
// })

export default router
