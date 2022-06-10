<template>
  <el-container style="min-height: 100vh">

    <el-aside :width="sideWidth + 'px'" style="box-shadow: 2px 0 6px rgb(0 21 41 / 35%);">
      <Aside :logoTextShow="logoTextShow" :isCollapse="isCollapse"/>
    </el-aside>

    <el-container>
      <el-header style="border-bottom: 1px solid #ccc">
        <Header :collapse="isCollapse" :collapseBtnClass="collapseBtnClass" :user="user"/>
      </el-header>

      <el-main>
       <router-view @refreshUser="getUser"/>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>

import request from "@/utils/request";
import Aside from "@/components/Aside";
import Header from "@/components/Header";
export default {
  name: 'Home',
  data() {
    return {
      collapseBtnClass: 'el-icon-s-fold',
      isCollapse: false,
      sideWidth: 200,
      logoTextShow: true,
      user: {},
    }
  },
  created(){
    this.getUser()
  },
  components:{
    Header,
    Aside
  },
  methods: {
    collapse() {  // 点击收缩按钮触发
      this.isCollapse = !this.isCollapse
      if (this.isCollapse) {  // 收缩
        this.sideWidth = 64
        this.collapseBtnClass = 'el-icon-s-unfold'
        this.logoTextShow = false
      } else {   // 展开
        this.sideWidth = 200
        this.collapseBtnClass = 'el-icon-s-fold'
        this.logoTextShow = true
      }
    },
    getUser(){
      let username = localStorage.getItem("user")? JSON.parse(localStorage.getItem("user")).username : ""
      // 从后台获取数据
      this.request.get("/user/username/"+username).then(res=>{
          this.user = res.data
      })
    }
  }
}
</script>

<style>

</style>
