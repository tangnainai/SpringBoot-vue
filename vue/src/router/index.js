import Vue from 'vue'
import VueRouter from 'vue-router'
import store from "@/store";
Vue.use(VueRouter)

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue')
  },
  {
    path: '*',
    name: 'NotFound',
    component: () => import('../views/NotFound.vue')
  },
  {
    path: '/blank',
    name: 'Blank',
    component: () => import('../views/Blank.vue')
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})
// 重置路由的方法
export const resetRouter =() =>{
  router.matcher = new VueRouter({
    mode: 'history',
    base: process.env.BASE_URL,
    routes
  })
}
// 动态路由
export const setRoutes = () =>{
  const storeMenus = localStorage.getItem("menus");
  if(storeMenus){
    // 获取当前路由对象名称数组
    const currentRouteNames = router.getRoutes().map(v =>v.name)
    if(!currentRouteNames.includes('Manage')){
      // 拼装路由
      const manageRoute = {path: '/',name:'Manage',component: () => import('../views/Manage.vue'),redirect: "/login",children: []}
      manageRoute.children.push({
        path: '/person',name: '个人信息',component: () => import('../views/Person.vue')
      })
      manageRoute.children.push({
        path: '/modify',name: '修改密码',component: () => import('../views/Modify.vue')
      })
      // 将字符串转换为JSON数据
      const menus = JSON.parse(storeMenus)
      menus.forEach(item =>{
        // 判断path是否存在
        if(item.path){
          let itemMenu = {path: item.path.replace("/",""),name:item.name,component: () => import('../views/'+item.pagePath+'.vue')}
          manageRoute.children.push(itemMenu)
        }else if(item.menuList.length){ // path不存在则判断 menuList 是否为空
          item.menuList.forEach(item =>{
            if(item.path){ // menuList不为空的情况下 判断 path是否存在
              let itemMenu = {path: item.path.replace("/",""),name:item.name,component: () => import('../views/'+item.pagePath+'.vue')}
              manageRoute.children.push(itemMenu)
            }
          })
        }
      })
      router.addRoute(manageRoute)
    } // 如果当前路由不存在则添加路由
  }
}
setRoutes();
// 路由守卫
router.beforeEach((to, from, next) => {
  localStorage.setItem("currentPathName", to.name)  // 设置当前的路由名称，为了在Header组件中去使用
  store.commit("setPath")  // 触发store的数据更新


  // 1.如果访问的是登录页面（无需权限），直接放行
  if (to.path === '/login' || to.path === '/register'){
    return next()
  }
  // 2.如果访问的是有登录权限的页面，先要获取token
  const tokenStr = localStorage.getItem('user')
  // 2.1如果token为空，强制跳转到登录页面；否则，直接放行
  if (!tokenStr) {
    alert("请登录")
    return next('/login')
  }
  next()  // 放行路由
})


export default router
