<template>
  <div>

    <div style="margin: 10px 0">
      <el-input v-model="name" style="width: 200px" placeholder="名称" suffix-icon="el-icon-search"></el-input>
     <el-button class="ml-5" type="primary" @click="load">搜索</el-button>
      <el-button type="warning" @click="reset">重置</el-button>
    </div>

    <div style="margin: 10px 0">
      <el-button type="primary" @click="HandleAdd()">新增 <i class="el-icon-circle-plus-outline"></i></el-button>
      <el-button type="danger" @click="delBatch">批量删除 <i class="el-icon-remove-outline"></i></el-button>
    </div>

    <el-table
        :data="tableData"
        border stripe
        :header-cell-class-name="headerBg"
        @selection-change="handleSelectionChange"
        row-key="id"
        default-expand-all
        :tree-props="{children: 'menuList', hasChildren: 'hasChildren'}">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column v-if="false" prop="id" label="id" width="140"></el-table-column>
      <el-table-column prop="name" label="名称" width="140"></el-table-column>
      <el-table-column prop="path" label="路径"></el-table-column>
      <el-table-column prop="pagePath" label="页面名称"></el-table-column>
      <el-table-column label="图标" align="center">
        <template slot-scope="scope">
          <i :class="scope.row.icon" style="font-size:20px"/>
        </template>
      </el-table-column>
      <el-table-column prop="description" label="描述"></el-table-column>
      <el-table-column label="操作"  width="300" align="center">
        <template slot-scope="scope">
          <el-button type="primary"  v-if="scope.row.pid===null&&(scope.row.path===''||scope.row.path===null)"
                     @click="HandleAdd(scope.row.id)">新增子菜单 <i class="el-icon-circle-plus-outline"></i></el-button>
          <el-button type="success" @click="handlEdit(scope.row)">编辑 <i class="el-icon-edit"></i></el-button>
          <el-button type="danger" @click="open(scope.row)">删除 <i class="el-icon-remove-outline"></i></el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog title="用户信息" :visible.sync="dialogFormVisible" width="30%">
      <el-form label-width="80px" :model="form"  :rules="rules" ref="formUser">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="路径" prop="path">
          <el-input v-model="form.path" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="页面名称" prop="pagePath">
          <el-input v-model="form.pagePath" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="图标">
          <el-select v-model="form.icon" placeholder="请选择" style="width: 100%">
            <el-option
                v-for="item in options"
                :key="item.name"
                :label="item.name"
                :value="item.value">
              {{item.name}}  <i :class="item.value"/>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <span style="margin-right: 10px;color: red">{{msg}}</span>
        <el-button @click="FormVisible">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import request from "@/utils/request";
import router from "@/router";

export default {
  name: "Role",
  data(){
    return {
      msg:'',
      id:0,
      name:'',
      description:'',
      path:'',
      icon:'',
      tableData: [],
      form: {},
      multipleSelection:[],
      dialogFormVisible: false,
      headerBg: 'headerBg',
      rules:{
        name:[
          {required: true, message: '请输入名称', trigger: 'blur'},
        ],
      },
      options:[],
    }
  },
  created() {
    this.load();
  },
  methods:{
    // 搜索框重置
    reset(){
      this.name=""
      this.description=""
      this.load()
    },
    // 删除
    open(row) {
      this.$confirm('此操作将永久删除该用户, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request.delete("/menu/"+row.id).then(res=>{
          if(res.code==='200'){
            this.$message.success("删除成功")
            this.load();
            let id = JSON.parse(localStorage.getItem("user")).id
            request.post("/menu/menus",id).then(res=>{
              if(res.code==='200'){
                localStorage.setItem("menus",JSON.stringify(res.data.menus))
                console.log(res.data.menus)
                this.$router.go(0)
              }
            })
          }else if(res.code==='600'){
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
    // 批量删除
    delBatch(){
      let ids = this.multipleSelection.map(v => v.id) // [{},{},{}] => [1,2,3]
      this.$confirm('此操作将永久删除用户, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request.post("/menu/del/batch",ids).then(res=>{
          this.$message({
            type: 'success',
            message: '批量删除成功!'
          });
          let id = JSON.parse(localStorage.getItem("user")).id
          request.post("/menu/menus",id).then(res=>{
            if(res.code==='200'){
              localStorage.setItem("menus",JSON.stringify(res.data.menus))
              console.log(res.data.menus)
              this.$router.go(0)
            }
          })
          this.load();
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消批量删除'
        });
      });
    },
    FormVisible(){
      this.dialogFormVisible = false
      this.load()
    },
    // 添加
    save(){
      this.$refs['formUser'].validate((valid)=>{
        if(valid){
          request.post("/menu",this.form).then(res=>{
            if(res.code === '200'){
              this.$message.success("保存成功")
              this.dialogFormVisible = false
              let id = JSON.parse(localStorage.getItem("user")).id
              request.post("/menu/menus",id).then(res=>{
                if(res.code==='200'){
                  localStorage.setItem("menus",JSON.stringify(res.data.menus))
                  console.log(res.data.menus)
                  this.$router.go(0)
                }
              })
            }else{
              this.msg = res.msg
            }
          })
        }else {
          return false;
        }
      })
    },
    // 编辑
    handlEdit(row){
      this.msg = ""
      this.diaologPassword = false;
      this.form = row;
      this.dialogFormVisible = true;
      request.get("/menu/icons").then(res=>{
        if(res.code==='200'){
          this.options = res.data
        }
      })
    },
    // 添加
    HandleAdd(pid){
      this.msg = ""
      this.diaologPassword = true
      this.dialogFormVisible = true
      this.form = {
        pid: pid
      }
      request.get("/menu/icons").then(res=>{
        console.log(res)
        if(res.code==='200'){
          this.options = res.data
        }
      })
    },
    // 查询
    load() {
      request.get("/menu/page", {params:{
        name:this.name,
        }}).then(res => {
        this.tableData = res.data
      })
    },
    // 获取批量删除的用户信息
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
    },
  },
}
</script>

<style scoped>

</style>