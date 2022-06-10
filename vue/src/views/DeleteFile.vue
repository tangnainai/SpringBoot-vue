<template>
  <div>
    <div style="margin: 10px 0">
      <el-input v-model="name" style="width: 200px" placeholder="请输入名称" suffix-icon="el-icon-search"></el-input>
      <el-button class="ml-5" type="primary" @click="load">搜索</el-button>
      <el-button type="warning" @click="reset">重置</el-button>
    </div>
    <div style="margin: 10px 0">
      <el-button type="danger" @click="thoroughlyBatch">批量删除<i class="el-icon-remove-outline"></i></el-button>
      <el-popconfirm
          class="ml-5"
          confirm-button-text='是的'
          cancel-button-text='取消'
          icon="el-icon-info"
          icon-color="red"
          title="是否恢复这些文件？"
          @confirm="refreshBatch"
      >
      <el-button type="success" slot="reference">批量恢复<i class="el-icon-refresh-left"></i></el-button>
      </el-popconfirm>
      <el-button type="info" style="margin-left: 10px" @click="empty">清空<i class="el-icon-delete"></i></el-button>
    </div>

    <el-table :data="tableData" border stripe  @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column v-if="false" prop="id" label="id">
      </el-table-column>
      <el-table-column prop="name" label="文件名" width="300">
      </el-table-column>
      <el-table-column prop="type" label="文件类型" width="100">
      </el-table-column>
      <el-table-column prop="size" label="文件大小" width="100">
      </el-table-column>
      <el-table-column prop="deleteTime" label="删除的时间">
      </el-table-column>
      <el-table-column label="操作"  width="200" align="center">
        <template slot-scope="scope">
          <el-popconfirm
              class="ml-5"
              confirm-button-text='是的'
              cancel-button-text='取消'
              icon="el-icon-info"
              icon-color="red"
              title="是否恢复这个文件？"
              @confirm="restore(scope.row.id)"
          >
            <el-button type="success" slot="reference">恢 复<i class="el-icon-refresh-left"></i></el-button>
          </el-popconfirm>
        <el-button type="danger" style="margin-left: 5px" @click="thoroughly(scope.row.id)">删 除<i class="el-icon-delete-solid"></i></el-button>
        </template>
      </el-table-column>
    </el-table>
    <div style="padding: 10px 0">
      <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :page-sizes="[5, 10, 15, 20]"
          :page-size="sizePage"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total">
      </el-pagination>
    </div>

  </div>
</template>

<script>
import request from "@/utils/request";
export default {
  name: "DeleteFile",
  data(){
    return{
      current: 1,
      sizePage: 10,
      total: 0,
      name: "",
      tableData: [],
      handleList: [],
    }
  },
  created() {
    this.load();
  },
  methods:{
    empty(){
      this.$confirm('此操作将永久删除该用户,不可再复原!是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(()=>{
        request.post("/file/empty").then(res=>{
          if(res.code==='200'){
            this.$message.success("删除成功")
            this.load();
          }else{
            this.$message.error(res.msg)
          }
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        });
      });
    },
    load(){
      request.get("/file/queryBatch",{
        params:{
          current: this.current,
          sizePage: this.sizePage,
          name: this.name
        }
      }).then(res=>{
        if(res.code==='200'){
          this.tableData = res.data.records
          this.total = res.data.total
        }
      })
    },
    handleSizeChange(res){
      this.sizePage = res
      this.load()
    },
    handleCurrentChange(res){
      this.current = res
      this.load()
    },
    reset(){
      this.name = ""
      this.load()
    },
    restore(id){
      request.post("/file/updateIsDelete",id).then(res=>{
        if(res.code==='200'){
          this.$message.success("恢复成功")
          this.load()
        }else{
          this.$message.error("恢复失败")
        }
      })
    },
    handleSelectionChange(res){
      this.handleList = res
    },
    refreshBatch(){
      let ids = this.handleList.map(v=>v.id)
      request.post("/file/updateBatch",ids).then(res=>{
        if(res.code==='200') {
          this.$message.success("恢复成功")
          this.load()
        }else {
          this.$message.error("恢复失败")
        }
      })
    },
    thoroughly(id){
      this.$confirm('此操作将永久删除该用户,不可再复原!是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(()=>{
        request.post("/file/thoroughlyDelete",id).then(res=>{
          if(res.code==='200'){
            this.$message.success("删除成功")
            this.load();
          }else{
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
    thoroughlyBatch(){
      let ids = this.handleList.map(v=>v.id)
      this.$confirm('此操作将永久删除该用户,不可再复原!是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(()=>{
        request.post("/file/thoroughlyBatch",ids).then(res=>{
          if(res.code==='200'){
            this.$message.success("删除成功")
            this.load();
          }else{
            this.$message.error("删除失败")
          }
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        });
      });
    }
  }
}
</script>

<style scoped>

</style>