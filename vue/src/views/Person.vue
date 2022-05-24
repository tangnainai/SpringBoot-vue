<template>
  <el-card style="width: 500px;">
    <el-form label-width="80px">
      <el-form-item label="用户名">
        <el-input v-model="form.username" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item v-if="diaologPassword" label="密码">
        <el-input v-model="form.password" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="昵称">
        <el-input v-model="form.nickname" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="邮箱">
        <el-input v-model="form.email" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="电话">
        <el-input v-model="form.phone" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="地址">
        <el-input v-model="form.address" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item style="text-align: right">
        <el-button type="primary" size="small"  autocomplete="off"  @click="$router.push('/user')">取 消</el-button>
        <el-button type="primary" size="small"  autocomplete="off"  @click="save">确 定</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script>
import request from "@/utils/request";

export default {
  name: "Person",
  data(){
    return{
      form: {},
    }
  },
  methods: {
    // 添加
    save(){
      request.post("/user/save",this.form).then(res=>{
        if(res){
          this.$router.push("/user")
          this.$message.success("保存成功")
        }else{
          this.$message.error("保存失败")
        }
      })
    },
  }
}
</script>

<style scoped>

</style>