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
        <el-button type="primary" @click="HandleAdd">新增 <i class="el-icon-circle-plus-outline"></i></el-button>
        <el-button type="danger" @click="delBatch">批量删除 <i class="el-icon-remove-outline"></i></el-button>
        <el-upload
            action="http://localhost:8088/user/import"
           :show-file-list="false"
            accept="xlsx"
           :on-success="handleExcelImportSuccess"
           style="display: inline-block">
          <el-button type="primary" class="ml-5">导入 <i class="el-icon-bottom"></i></el-button>
        </el-upload>
        <el-button type="primary" @click="HandleExport" class="ml-5">导出 <i class="el-icon-top"></i></el-button>
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
            <el-button type="success" @click="handlEdit(scope.row)">编辑 <i class="el-icon-edit"></i></el-button>
            <el-button type="danger" @click="open(scope.row)">删除 <i class="el-icon-remove-outline"></i></el-button>
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

      <el-dialog title="用户信息" :visible.sync="dialogFormVisible" width="30%">
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
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="dialogFormVisible = false">取 消</el-button>
          <el-button type="primary" @click="save">确 定</el-button>
        </div>
      </el-dialog>

    </div>
</template>

<script>
import request from "@/utils/request";

export default {
  name: "User",
  data(){
    return {
      diaologPassword:false,
      id:0,
      email: "",
      address: "",
      username: "",
      total: 0,
      tableData: [],
      current: 1,
      form: {},
      size: 10,
      multipleSelection:[],
      dialogFormVisible: false,
      headerBg: 'headerBg'
    }
  },
  created() {
    this.load();
  },
  methods:{
    reset(){
      this.username=""
      this.email=""
      this.address=""
      this.load()
    },
    handleExcelImportSuccess(){
      this.$message.success("导入成功")
      this.load()
    },
    HandleExport(){
      window.open("http://localhost:8088/user/export")
    },
    open(row) {
      this.$confirm('此操作将永久删除该用户, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request.post("/user/open",row.id).then(res=>{
          this.$message({
            type: 'success',
            message: '删除成功!'
          });
          this.load();
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        });
      });
    },
    delBatch(){
      let ids = this.multipleSelection.map(v => v.id) // [{},{},{}] => [1,2,3]
      this.$confirm('此操作将永久删除用户, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request.post("/user/openMap",ids).then(res=>{
          this.$message({
            type: 'success',
            message: '批量删除成功!'
          });
          this.load();
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消批量删除'
        });
      });
    },
    // 添加
    save(){
      request.post("/user/save",this.form).then(res=>{
        if(res){
          this.$message.success("保存成功")
          this.dialogFormVisible = false
          this.load()
        }else{
          this.$message.error("保存失败")
        }
      })
    },
    // 编辑
    handlEdit(row){
      this.diaologPassword = false;
      this.form = row;
      this.dialogFormVisible = true;
    },
    HandleAdd(){
      this.diaologPassword = true
      this.dialogFormVisible = true
      this.form = {}
    },
    load() {
      request.get("/user/page",{
        params:{
          current: this.current,
          size: this.size,
          username: this.username,
          email: this.email,
          address: this.address,
          id:this.id,
        }
      }).then(res => {
        // console.log(res)
        this.tableData = res.records
        this.total = res.total
      })
    },
    handleSelectionChange(val) {
      // console.log(val);
      this.multipleSelection = val;
    },
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
  },
}
</script>

<style scoped>
.headerBg {
  background: #eee!important;
}
</style>