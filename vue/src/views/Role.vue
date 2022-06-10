<template>
  <div>

    <div style="margin: 10px 0">
      <el-input v-model="name" style="width: 200px" placeholder="角色名" suffix-icon="el-icon-search"></el-input>
      <el-input v-model="description" style="width: 200px" placeholder="描述" suffix-icon="el-icon-message" class="ml-5"></el-input>
     <el-button class="ml-5" type="primary" @click="load">搜索</el-button>
      <el-button type="warning" @click="reset">重置</el-button>
    </div>

    <div style="margin: 10px 0">
      <el-button type="primary" @click="HandleAdd">新增 <i class="el-icon-circle-plus-outline"></i></el-button>
      <el-button type="danger" @click="delBatch">批量删除 <i class="el-icon-remove-outline"></i></el-button>
    </div>

    <el-table :data="tableData" border stripe :header-cell-class-name="headerBg" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column v-if="false" prop="id" label="id" width="140">
      </el-table-column>
      <el-table-column prop="name" label="角色名" width="140">
      </el-table-column>
      <el-table-column prop="flag" label="标识" width="250">
      </el-table-column>
      <el-table-column prop="description" label="描述">
      </el-table-column>
      <el-table-column prop="time" label="修改时间" width="150">
      </el-table-column>
      <el-table-column label="操作"  width="300" align="center">
        <template v-if="!(scope.row.flag==='ROLE_ADMIN')" slot-scope="scope">
          <el-button type="info" @click="menuDistribution(scope.row)">分配菜单 <i class="el-icon-s-grid"></i></el-button>
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
      <el-form label-width="80px" :model="form"  :rules="rules" ref="formUser">
        <el-form-item label="角色名" prop="name">
          <el-input v-model="form.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="标识" prop="flag">
        <el-input v-model="form.flag" autocomplete="off"></el-input>
       </el-form-item>
        <el-form-item v-if="false" label="标识" prop="flagRole">
          <el-input v-model="form.flagRole" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <span style="margin-right: 10px;color: red">{{msg}}</span>
        <el-button @click="FormVisible">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="用户信息" :visible.sync="menuFormVisible" width="20%">
      <el-tree
          :data="menuList"
          show-checkbox
          node-key="id"
          ref="tree"
          :default-expanded-keys="[1,3]"
          :props="defaultProps">
      <span class="custom-tree-node" slot-scope="{ node, data }">
        <span><i :class="data.icon"/>{{ data.name }}</span>
      </span>
      </el-tree>
      <div slot="footer" class="dialog-footer">
        <span style="margin-right: 10px;color: red">{{msg}}</span>
        <el-button @click="FormVisible">取 消</el-button>
        <el-button type="primary" @click="saveMenu">确 定</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import request from "@/utils/request";

export default {
  name: "Role",
  data(){
    return {
      defaultProps:{
        children: 'menuList',
        label: 'name'
      },
      msg:'',
      id:0,
      name:'',
      description:'',
      time:'',
      total: 0,
      tableData: [],
      current: 1,
      form: {},
      size: 10,
      multipleSelection:[],
      dialogFormVisible: false,
      menuFormVisible:false,
      headerBg: 'headerBg',
      rules:{
        name:[
          {required: true, message: '请输入角色名', trigger: 'blur'},
        ],
        flag:[
          {required: true, message: '请输入标识', trigger: 'blur'},
        ]
      },
      menuList:[],
      roleId:0,
      flagRole: '',
    }
  },
  created() {
    this.load();
  },
  methods:{
    // 分配菜单
    menuDistribution(row){
      this.roleId = row.id
      this.flagRole = row.flag
      this.menuFormVisible = true
      request.get("/menu/page",{params:{
          name:''
        }}).then(res=>{
        this.menuList = res.data
      })
      request.get("/role/roleMenu",{params:{
          roleId: row.id
        }}).then(res=>{
          if(res.code==='200'){
            this.$refs.tree.setCheckedKeys(res.data)
          }
      })
    },
    // 确认分配菜单
    saveMenu(){
      let menuId = this.$refs.tree.getCheckedKeys()
      let roleFrom = {
        roleId: this.roleId,
        menuId: menuId
      }
      request.post("/role/roleMenu",roleFrom).then(res=>{
         if(res.code==='200'){
           if(this.flagRole==='ROLE_ADMIN'){
            this.$store.commit("loginOut")
            this.$message.success("菜单分配成功请从新登录")
           }else{
             this.$message.success("分配"+this.flagRole+"菜单成功")
           }
         }else {
           this.$message.error("菜单分配失败")
         }
      })
      this.menuFormVisible = false
    },
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
        request.delete("/role/"+row.id).then(res=>{
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
        request.post("/role/del/batch",ids).then(res=>{
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
      this.menuFormVisible = false
      this.load()
    },
    // 添加
    save(){
      this.$refs['formUser'].validate((valid)=>{
        if(valid){
          request.post("/role", this.form).then(res=>{
            if(res.code === '200'){
              this.$message.success("保存成功")
              this.dialogFormVisible = false
              this.load()
            }else if(res.code==='500'){
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
      this.form.flagRole = row.flag
      console.log(this.form)
      this.dialogFormVisible = true;
    },
    // 添加
    HandleAdd(){
      this.msg = ""
      this.diaologPassword = true
      this.dialogFormVisible = true
      this.form = {}
    },
    // 分页查询
    load() {
      request.get("/role",{
        params:{
          current: this.current,
          size: this.size,
          name: this.name,
          description: this.description
        }
      }).then(res => {
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

</style>