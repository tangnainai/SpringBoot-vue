<template>
  <el-menu style="min-height: 100%; overflow-x: hidden"
           background-color="rgb(48, 65, 86)"
           text-color="#fff"
           active-text-color="#ffd04b"
           :collapse-transition="false"
           :collapse="isCollapse"
           router
           :default-openeds="['1']"
  >
    <div style="height: 60px; line-height: 60px; text-align: center">
      <img src="../assets/logo.png" alt="" style="width: 20px; position: relative; top: 5px; right: 5px">
      <b style="color: white" v-show="logoTextShow">后台管理系统</b>
    </div>
    <div v-for="item in menus" :key="item.id">
      <div v-if="item.path">
        <el-menu-item :index="item.path+''">
            <i :class="item.icon"></i>
            <span slot="title">{{ item.name }}</span>
        </el-menu-item>
      </div>
      <div v-else>
        <el-submenu :index="item.id+''">
          <template slot="title">
            <i :class="item.icon"></i>{{ item.name }}
          </template>
          <div v-for="menuList in item.menuList" :key="menuList.id">
            <el-menu-item :index="menuList.path">
              <i :class="menuList.icon"></i>
              <span slot="title">{{menuList.name}}</span>
            </el-menu-item>
          </div>
        </el-submenu>
      </div>
    </div>
  </el-menu>
</template>

<script>
export default {
  name: "Aside",
  props: {
    isCollapse: Boolean,
    logoTextShow: Boolean
  },
  data(){
    return{
      menus: []
    }
  },
  created(){
    this.menus = localStorage.getItem("menus") ? JSON.parse(localStorage.getItem("menus")):[]
  }
}
</script>

<style scoped>

</style>