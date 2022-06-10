<template>
  <el-card style="width: 500px;">
    <el-form label-width="80px" :model="form"  :rules="rules" ref="formUser">
      <el-upload
          class="avatar-uploader"
          :action="'http://'+serverIp+':8088/file/upload'"
          :show-file-list="false"
          :on-success="handleAvatarSuccess"
      >
        <img v-if="form.avatarUrl" :src="form.avatarUrl" class="avatar">
        <i v-else class="el-icon-plus avatar-uploader-icon"></i>
      </el-upload>
      <el-form-item label="用户名" prop="username">
        <el-input v-model="form.username" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="昵称" prop="nickname">
        <el-input v-model="form.nickname" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="form.email" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="电话" prop="phone">
        <el-input v-model="form.phone" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="地址" prop="address">
        <el-input v-model="form.address" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item style="text-align: right">
        <span style="margin-right: 10px; color: red">{{mag}}</span>
        <el-button type="primary" size="small"  autocomplete="off"  @click="$router.push('/home')">返 回</el-button>
        <el-button type="primary" size="small"  autocomplete="off"  @click="save">保 存</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script>
import request from "@/utils/request";
import {serverIp} from "../../public/config";

export default {
  name: "Person",
  data(){
    return{
      serverIp: serverIp,
      mag:"",
      form: {},
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {},
      avatarUrl: "",
      rules:{
        username:[
          {required: true, message: '请输入用户名', trigger: 'blur'},
        ],
        password:[
          {required: true, message: '请输入密码', trigger: 'blur'},
        ],
        nickname:[
          {required: true, message: '请输入昵称', trigger: 'blur'},
        ],
        email:[
          {required: true, message: '请输入邮箱', trigger: 'blur'},
        ],
        phone:[
          {required: true, message: '请输入电话号', trigger: 'blur'},
        ],
        address:[
          {required: true, message: '请输入地址', trigger: 'blur'},
        ],
      }
    }
  },
  created() {
    this.mag = ""
    this.request.get("/user/username/"+this.user.username).then(res=>{
      if(res.code === '200'){
        this.form = res.data;
      }
    })
  },
  methods: {
    // 修改个人信息
    async getUser(){
      return (await this.request.get("/user/username/"+this.user.username)).data
    },
    // 添加
    save(){
      this.$refs['formUser'].validate((valid)=>{
        if(valid){
          request.post("/user/save",this.form).then(res=>{
            if(res.code === '200'){
              this.$message.success("保存成功")
              // 触发父级更新的user方法
              this.$emit("refreshUser")
              // this.$router.push("/user")
              // this.load()
              // 更新浏览器缓存
              this.getUser().then(res=>{
                res.token = JSON.parse(localStorage.getItem("user")).token
                localStorage.setItem("user",JSON.stringify(res))
              })
            }else{
              this.mag = res.msg
            }
          })
        }else {
          return false;
        }
      })
    },
    handleAvatarSuccess(res){
      this.form.avatarUrl = res
    }
  }
}
</script>

<style scoped>
.avatar-uploader {
  text-align: center;
  padding-bottom: 10px;
}
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 138px;
  height: 138px;
  line-height: 138px;
  text-align: center;
}
.avatar {
  width: 138px;
  height: 138px;
  display: block;
}
</style>