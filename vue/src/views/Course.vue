<template>
  <div>

    <div style="margin: 10px 0">
      <el-input v-model="name" style="width: 200px" placeholder="课程名" suffix-icon="el-icon-search"></el-input>
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
      <el-table-column prop="name" label="课程名">
      </el-table-column>
      <el-table-column prop="score" label="学分">
      </el-table-column>
      <el-table-column prop="times" label="课时">
      </el-table-column>
      <el-table-column prop="state" label="是否开课">
      </el-table-column>
      <el-table-column prop="teacher" label="授课老师" width="150">
      </el-table-column>
      <el-table-column label="操作"  width="300" align="center">
        <template v-if="!(scope.row.flag==='ROLE_ADMIN')" slot-scope="scope">
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

    <el-dialog title="课程信息" :visible.sync="dialogFormVisible" width="30%">
      <el-form label-width="80px" :model="form" ref="formUser">
        <el-form-item label="课程名" prop="name">
          <el-input v-model="form.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="学分" prop="score">
          <el-input v-model="form.score" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="课时" prop="times">
          <el-input v-model="form.times" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="授课老师" prop="teacher">
          <el-select v-model="form.teacherId" placeholder="请选择" style="width: 100%">
            <el-option v-for="item in teacher" :key="item.id" :label="item.nickname" :value="item.id">
            </el-option>
          </el-select>
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

export default {
  name: "Course",
  data(){
    return {
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
      headerBg: 'headerBg',
      roleId:0,
      flagRole: '',
      teacher:[],
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
        request.delete("/course/"+row.id).then(res=>{
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
        request.post("/course/del/batch",ids).then(res=>{
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
      request.post("/course", this.form).then(res=>{
        if(res.code === '200'){
          this.$message.success("保存成功")
          this.dialogFormVisible = false
          this.load()
        }else if(res.code==='500'){
          this.msg = res.msg
        }
      })
    },
    // 编辑
    handlEdit(row){
      this.msg = ""
      this.diaologPassword = false;
      this.form = row;
      this.form.flagRole = row.flag
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
      request.get("/course/username/ROLE_TEACHER").then(res=>{
        if(res.code==='200'){
          this.teacher = res.data
        }
      })
      request.get("/course/page",{
        params:{
          current: this.current,
          size: this.size,
          name: this.name,
        }
      }).then(res => {
        console.log(res)
        this.tableData = res.data.records
        this.total = res.data.total
      })
    },
    FormVisible(){
      this.dialogFormVisible =false
      this.load();
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