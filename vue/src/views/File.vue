<template>
  <div>
    <div style="margin: 10px 0">
      <el-input v-model="name" style="width: 200px" placeholder="请输入名称" suffix-icon="el-icon-search"></el-input>
     <el-button class="ml-5" type="primary" @click="load">搜索</el-button>
      <el-button type="warning" @click="reset">重置</el-button>
      <el-popconfirm
          class="ml-5"
          confirm-button-text='好的'
          cancel-button-text='取消'
          icon="el-icon-info"
          icon-color="red"
          title="确定要删除这些文件吗？"
          @confirm="delBatch"
      >
      <el-button v-if="userRole==='ROLE_ADMIN'" type="danger" slot="reference">批量删除 <i class="el-icon-remove-outline"></i></el-button>
      </el-popconfirm>
      <el-upload
          :action="'http://'+serverIp+':8088/file/upload'"
          :show-file-list="false"
          accept="xlsx"
          :on-success="handleUpload"
          style="display: inline-block;margin-left: 10px">
      <el-button type="primary">上 传 <i class="el-icon-top"></i></el-button>
      </el-upload>
      <el-upload
          :action="'http://'+serverIp+':8088/file/upload'"
          :show-file-list="false"
          accept="xlsx"
          style="display: inline-block; margin-left: 10px"
          multiple
          :on-success="handleUploadList"
          :file-list="fileList">
        <el-button type="primary">批量上传 <i class="el-icon-top"></i></el-button>
      </el-upload>
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
      <el-table-column label="下载" width="100">
        <template slot-scope="scope">
          <el-button type="primary" @click="download(scope.row.url)">下载</el-button>
        </template>
      </el-table-column>
      <el-table-column label="启用">
        <template slot-scope="scope">
          <el-switch v-model="scope.row.enable" active-color="#13ce66" inactive-color="#ccc" @change="changeEnable(scope.row)"></el-switch>
        </template>
      </el-table-column>
      <el-table-column v-if="userRole==='ROLE_ADMIN'" label="操作"  width="200" align="center">
        <template slot-scope="scope">
          <el-popconfirm
              class="ml-5"
              confirm-button-text='好的'
              cancel-button-text='取消'
              icon="el-icon-info"
              icon-color="red"
              title="确定要删除这个文件吗？"
              @confirm="open(scope.row.id)"
          >
          <el-button type="danger" slot="reference">删 除<i class="el-icon-remove-outline"></i></el-button>
          </el-popconfirm>
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
import {serverIp} from "../../public/config";
export default {
  name: "File",
  data(){
    return{
      userRole: JSON.parse(localStorage.getItem("user")).role,
      serverIp: serverIp,
      name: "",
      type: "",
      size: "",
      url: "",
      enable: 0,
      value: true,
      sizePage:10,
      total: 0,
      current:1,
      tableData:[],
      handleSelection:[],
      fileList:[]
    }
  },
  created() {
    this.load();
  },
  methods:{
    // 重置搜索框
    reset(){
      this.name = "";
      this.load();
    },
    // 搜索
    load(){
      request.get("/file/query",{
        params:{
          name: this.name,
          current: this.current,
          sizePage: this.sizePage
        }
      }).then(res=>{
        // console.log(res)
        if(res.code==='200'){
          this.tableData = res.data.records
          this.total = res.data.total
        }
      })
    },
    handleSizeChange(sizePage){
      this.sizePage = sizePage
      this.load();
    },
    handleCurrentChange(current){
      this.current = current;
      this.load()
    },
    // 下载
    download(url){
      window.open(url)
    },
    // 修改
    changeEnable(row){
      request.post("/file/updateEnable",row).then(res=>{
        if(res.code==='200'){
          this.$message.success("操作成功");
        }
      })
    },
    // 删除
    open(id){
      request.post("/file/removeById",id).then(res=>{
        // console.log(res)
        if(res.code==='200'){
          this.$message.success("删除成功")
          this.load()
        }else{
          this.$message.info("取消删除")
        }
      })
    },
    handleSelectionChange(val){
      this.handleSelection = val
    },
    // 批量删除
    delBatch(){
      let ids = this.handleSelection.map(v=>v.id)
      request.post("/file/delBatch",ids).then(res=>{
        console.log(res)
        if(res.code==='200'){
          this.$message.success("删除"+ids.length+"成功")
          this.load()
        }else{
          this.$message.error("删除失败")
        }
      })
    },
    // 上传
    handleUpload(res){
      if(res!=null){
        this.$message.success("上传成功")
        this.load()
      }else{
        this.$message.info("上传失败")
      }
    },
    // 批量上传
    handleUploadList(res){
      if(res!=null){
        this.$message.success("上传成功")
        this.load()
      }else {
        this.$message.info("上传失败")
      }
    },
  }
}
</script>

<style scoped>

</style>