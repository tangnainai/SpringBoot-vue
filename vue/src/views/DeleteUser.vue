<template>
  <div>
    <div style="margin: 10px 0">
      <el-input v-model="username" style="width: 200px" placeholder="请输入名称" suffix-icon="el-icon-search"></el-input>
      <el-input v-model="email" style="width: 200px" placeholder="请输入邮箱" suffix-icon="el-icon-message" class="ml-5"></el-input>
      <el-input v-model="address" style="width: 200px" placeholder="请输入地址" suffix-icon="el-icon-position" class="ml-5"></el-input>
      <el-button class="ml-5" type="primary" @click="load">搜索</el-button>
      <el-button type="warning" @click="reset">重置</el-button>
    </div>

    <div style="margin: 10px 0">
      <el-button type="danger" @click="delBatch">批量删除<i class="el-icon-remove-outline"></i></el-button>
      <el-button type="success" @click="refresh">批量恢复<i class="el-icon-refresh-left"></i></el-button>
      <el-button type="info" @click="empty">清空<i class="el-icon-delete"></i></el-button>
    </div>

    <el-table :data="tableData" border stripe :header-cell-class-name="headerBg"  @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column v-if="false" prop="id" label="id" width="140">
      </el-table-column>
      <el-table-column prop="username" label="用户名" width="140">
      </el-table-column>
      <el-table-column prop="email" label="邮箱">
      </el-table-column>
      <el-table-column prop="nickname" label="昵称">
      </el-table-column>
      <el-table-column prop="phone" label="电话">
      </el-table-column>
      <el-table-column prop="address" label="地址">
      </el-table-column>
      <el-table-column label="操作"  width="200" align="center">
        <template slot-scope="scope">
          <el-button type="success" @click="handlEdit(scope.row)">恢 复<i class="el-icon-refresh-left"></i></el-button>
          <el-button type="danger" @click="open(scope.row)">删 除<i class="el-icon-delete"></i></el-button>
        </template>
      </el-table-column>
    </el-table>
    <div style="padding: 10px 0">
      <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :page-sizes="[5, 10, 15, 20]"
          :page-size="size"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total">
      </el-pagination>
    </div>
  </div>
</template>

<script>
import request from "@/utils/request";

export default {
  name: "DeleteUser",
  data(){
    return{
      username: "",
      email:"",
      address: "",
      size:10,
      total:0,
      current:1,
      tableData: [],
      headerBg: 'headerBg',
      multipleSelection: [], // 批量删除用户信息
    }
  },
  created() {
    this.load();
  },
  methods:{
    // 清空回收站
    empty(){
      this.$confirm('此操作将清空所有数据,不可再复原!是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(()=>{
        this.request.post("/user/empty").then(res=>{
          if(res.data===true){
            this.load();
            this.$message.success('回收站清空成功')
          }else{
            this.$message.error("回收站没有数据,不需要清空")
          }
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        });
      });
    },
    // 获取批量删除的用户信息
    handleSelectionChange(val) {
      // console.log(val);
      this.multipleSelection = val;
    },
    delBatch(){
      let ids = this.multipleSelection.map(v=>v.id)
      this.$confirm('此操作将永久删除该用户,不可再复原!是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(()=>{
        this.request.post("/user/deleteBatch",ids).then(res=>{
          if(res.code==='200'){
            this.load();
            this.$message.success('删除成功')
          }else {
            this.$message.error("删除失败")
          }
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        });
      });
    },
    // 批量恢复
    refresh(){
      let user = this.multipleSelection
      this.request.post("/user/updateBatch",user).then(res=>{
        if(res.code==='200'){
          this.load();
          this.$message.success("恢复成功");
        }else if(res.code==='600'){
          this.load();
          this.$message.error(res.msg);
        }
      })
    },
    // 恢复
    handlEdit(row){
      this.request.post("/user/updateDeleted",row).then(res=>{
        if(res.code==='200'){
          this.load()
          this.$message.success("恢复成功")
        }else {
          this.$message.error(res.msg)
        }
      })
    },
    // 删除
    open(row){
      this.$confirm('此操作将永久删除该用户,不可再复原!是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request.post("/user/deletedById",row.id).then(res=>{
          if(res.code === '200') {
            this.$message({
              type: 'success',
              message: '删除成功!'
            });
            this.load();
          }
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        });
      });
    },
    // 重置
    reset(){
      this.username = "";
      this.email = "";
      this.address = "";
      this.load();
    },
    // 查询数据
    load(){
      this.request.get("/user/deletePage",{
        params:{
          current: this.current,
          size: this.size,
          username: this.username,
          email: this.email,
          address: this.address,
        }
      }).then(res=>{
        // console.log(res)
        this.tableData = res.data.records
        this.total = res.data.total
      })
    },
    // 点击页面
    handleSizeChange(size){
      // console.log(size)
      this.size = size
      this.load();
    },
    handleCurrentChange(current){
      // console.log(current)
      this.current = current;
      this.load();
    }
  }
}
</script>

<style scoped>

</style>