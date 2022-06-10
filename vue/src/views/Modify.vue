<template>
    <el-card style="width: 500px;">
      <el-form :model="user" :rules="rules" ref="userForm">
        <el-form-item prop="oldPassword">
          <el-input placeholder="请输入旧密码" size="medium" style="margin: 5px 0" prefix-icon="el-icon-lock"  show-password  v-model="user.oldPassword"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input placeholder="请输入新密码" size="medium" style="margin: 5px 0" prefix-icon="el-icon-lock" show-password v-model="user.password"></el-input>
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input placeholder="请确认密码" size="medium" style="margin: 5px 0" prefix-icon="el-icon-lock" show-password v-model="user.confirmPassword"></el-input>
        </el-form-item>
        <el-form-item style="margin: 5px 0; text-align: right">
          <span style="margin-right: 20px; color: red">{{msg}}</span>
          <el-button type="info" size="small"  autocomplete="off" @click="$router.push('/home')">取消</el-button>
          <el-button type="primary" size="small"  autocomplete="off" @click="determine">确定</el-button>
        </el-form-item>
      </el-form>
    </el-card>
</template>

<script>
import request from "@/utils/request";

export default {
  name: "Modify",
  data(){
    return{
      user:{},
      msg:'',
      rules: {
        oldPassword: [
          { required: true, message: '请输入旧密码', trigger: 'blur' },
          { min: 3, max: 15, message: '长度在 3 到 15 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请再次输入确认密码', trigger: 'blur' },
          { min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur' }
        ],
      }
    }
  },
  methods:{
    determine(){
      this.$refs['userForm'].validate((valid)=> {
        if(valid){
          let user = JSON.parse(localStorage.getItem("user"))
          let passwordOld = user.password
          let id = user.id
          if (this.user.oldPassword === passwordOld) {
            if (this.user.password === this.user.confirmPassword) {
              this.user = {
                id: id,
                password: this.user.password
              }
              request.post("/user/modify",this.user).then(res=>{
                if(res.code==='200'){
                  this.$message.success("修改密码成功请从新登录")
                  this.$router.push("/login")
                  localStorage.removeItem("user")
                }
              })
            } else {
              this.$message.error("两次密码不相同")
            }
          } else {
             this.$message.error("旧密码错误")
          }
        }
      })
    }
  }
}
</script>

<style scoped>

</style>