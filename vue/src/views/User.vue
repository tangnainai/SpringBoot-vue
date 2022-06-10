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
        <el-button v-if="userRole==='ROLE_ADMIN'" type="primary" @click="HandleAdd">新增 <i class="el-icon-circle-plus-outline"></i></el-button>
        <el-button v-if="userRole==='ROLE_ADMIN'" type="danger" @click="delBatch">批量删除 <i class="el-icon-remove-outline"></i></el-button>
        <el-upload
            :action="'http://'+serverIp+':8088/user/import'"
            :show-file-list="false"
            accept="xlsx"
            :on-success="handleExcelImportSuccess"
            :on-error="onErroe"
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
        <el-table-column prop="role" label="角色">
         <template slot-scope="scope">
           <el-tag type="primary" v-if="scope.row.role==='ROLE_ADMIN'">管理员</el-tag>
           <el-tag type="primary" v-if="scope.row.role==='ROLE_STUDENT'">学生</el-tag>
           <el-tag type="primary" v-if="scope.row.role==='ROLE_TEACHER'">老师</el-tag>
         </template>
        </el-table-column>
        <el-table-column prop="nickname" label="昵称">
        </el-table-column>
        <el-table-column prop="phone" label="电话">
        </el-table-column>
        <el-table-column prop="address" label="地址">
        </el-table-column>
        <el-table-column v-if="userRole==='ROLE_ADMIN'" label="操作"  width="300" align="center">
          <template slot-scope="scope">
            <el-button v-if="!(scope.row.role==='ROLE_ADMIN')&&(scope.row.role==='ROLE_TEACHER')" type="primary" @click="courseEdit(scope.row)">查看课程<i class="el-icon-s-order"/></el-button>
            <el-button v-if="!(scope.row.role==='ROLE_ADMIN')" type="success" @click="handlEdit(scope.row)">编辑 <i class="el-icon-edit"></i></el-button>
            <el-button v-if="!(scope.row.role==='ROLE_ADMIN')" type="danger" @click="open(scope.row)">删除 <i class="el-icon-remove-outline"></i></el-button>
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
        <el-form label-width="80px" :model="form"  :rules="rules" ref="formUser">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item v-if="diaologPassword" label="密码" prop="password">
            <el-input v-model="form.password" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item label="角色">
            <el-select v-model="form.role" placeholder="请选择" style="width: 100%">
              <el-option
                  v-for="item in roles"
                  :key="item.name"
                  :label="item.name"
                  :value="item.flag">
                {{item.name}}  <i :class="item.value"/>
              </el-option>
            </el-select>
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
        </el-form>
        <div slot="footer" class="dialog-footer">
          <span style="margin-right: 10px;color: red">{{msg}}</span>
          <el-button @click="FormVisible">取 消</el-button>
          <el-button type="primary" @click="save">确 定</el-button>
        </div>
      </el-dialog>

      <el-dialog title="授课信息" :visible.sync="courseFlag" width="30%">
        <el-table label-width="80px" :data="course" border stripe>
          <el-table-column prop="name" label="课程名称"></el-table-column>
          <el-table-column prop="score" label="学分"></el-table-column>
          <el-table-column prop="times" label="学时"></el-table-column>
        </el-table>
        <div slot="footer" class="dialog-footer">
          <el-button @click="courseVisible">关 闭</el-button>
        </div>
      </el-dialog>
    </div>
</template>

<script>
import request from "@/utils/request";
import {serverIp} from "../../public/config";
export default {
  name: "User",
  data(){
    return {
      courseFlag: false,
      userRole: localStorage.getItem("user")?JSON.parse(localStorage.getItem("user")).role:'',
      serverIp: serverIp,
      msg:'',
      diaologPassword:false,
      id:0,
      email: "",
      address: "",
      nickname: "",
      username: "",
      total: 0,
      tableData: [],
      current: 1,
      form: {},
      size: 10,
      multipleSelection:[],
      dialogFormVisible: false,
      headerBg: 'headerBg',
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
      },
      roles:[],
      course:[],
    }
  },
  created() {
    this.load();
  },
  methods:{
    // 搜索框重置
    reset(){
      this.username=""
      this.email=""
      this.address=""
      this.load()
    },
    courseVisible(){
      this.courseFlag = false
    },
    courseEdit(row){
      this.courseFlag = true
      request.get("/user/course/"+row.id).then(res=>{
        if(res.code==='200'){
          this.course = res.data
        }
      })
    },
    // 导入失败
    onErroe(err, file, fileList){
      this.$message.error("上传文件类型错误,只能上传 xlsx 类型文件")
    },
    // 导入
    handleExcelImportSuccess(row){
      if(row.code==='200'){
        this.$message.success("导入成功")
      }else if(row.code==='400'){
        this.$message.warning(row.msg)
      }
      this.load();
    },
    // 导出
    HandleExport(){
      window.open(`http://${serverIp}:8088/user/export?`+'role='+this.userRole)
    },
    // 删除
    open(row) {
      this.$confirm('此操作将永久删除该用户, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request.post("/user/open",row.id).then(res=>{
          if(res.code==='200'){
            this.$message.success("删除成功")
            this.load();
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
    FormVisible(){
      this.dialogFormVisible = false
      this.load()
    },
    // 添加
    save(){
      this.$refs['formUser'].validate((valid)=>{
        if(valid){
          request.post("/user/save",this.form).then(res=>{
            if(res.code === '200'){
              this.$message.success("保存成功")
              this.dialogFormVisible = false
              this.load()
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
      this.request.get("/role/list").then(res=>{
        if(res.code==='200'){
          this.roles = res.data
        }
      })
    },
    // 添加
    HandleAdd(){
      this.msg = ""
      this.diaologPassword = true
      this.dialogFormVisible = true
      this.form = {}
      this.request.get("/role/list").then(res=>{
        if(res.code==='200'){
          this.roles = res.data
        }
      })
    },
    // 分页查询
    load() {
      request.get("/user/page",{
        params:{
          current: this.current,
          size: this.size,
          username: this.username,
          email: this.email,
          address: this.address,
          id: JSON.parse(localStorage.getItem("user")).id,
          role: JSON.parse(localStorage.getItem("user")).role
          // id:this.id,
        }
      }).then(res => {
        // console.log(res)
        this.tableData = res.data.records
        this.total = res.data.total
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
.headerBg {
  background: #eee!important;
}
</style>