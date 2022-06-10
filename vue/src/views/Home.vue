<template>
  <div>
    <el-row :gutter="10" style="margin-bottom: 40px">
      <el-col :span="6">
        <el-card>
          <div style="color: #409EFF"><i class="el-icon-user"/> 用户总数</div>
          <div style="padding: 10px 0; text-align: center;font-weight: bold;">200000</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div style="color: #fc8403"><i class="el-icon-coin"/> 销售总量</div>
          <div style="padding: 10px 0; text-align: center;font-weight: bold;">20</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div style="color: rgb(2,177,19)"><i class="el-icon-money"/> 收益总额</div>
          <div style="padding: 10px 0; text-align: center;font-weight: bold;">20</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div style="color: #ff9f29"><i class="el-icon-s-shop"/> 门店总量</div>
          <div style="padding: 10px 0; text-align: center;font-weight: bold;">20</div>
        </el-card>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="12">
        <div id="main" style="width: 500px; height: 400px"></div>
      </el-col>
      <el-col :span="12">
        <div id="pie" style="height: 400px"></div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import request from "@/utils/request";
export default {
  name: "Home",
  data(){
    return{

    }
  },
  mounted() {
    var chartDom = document.getElementById('main');
    var myChart = echarts.init(chartDom);
    var optionMain = {
      title:{
        text:'各季度会员数量统计',
      },
      xAxis: {
        type: 'category',
        data: ['第一季度','第二季度','第三季度','第四季度']
      },
      yAxis: {
        type: 'value'
      },
      tooltip: {
        trigger: 'axis',
        formatter: function (e,t,h){
          return "会员数：" + e[0].data
        }
      },
      series: [
        {
          data: [],
          type: 'line'
        }
      ]
    };

    var pieDom = document.getElementById('pie');
    var pieChart = echarts.init(pieDom);
    var optionPie = {
      title: {
        text: '各季度会员统计',
        subtext: '比例表',
        left: 'center'
      },
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b} : {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        left: 'left'
      },
      series: [
        {
          name: '会员数',
          type: 'pie',
          radius: '40%',
          center: ['25%', '50%'],
          data: [],
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        },
        {
          name: '会员数',
          type: 'pie',
          radius: '40%',
          center: ['60%', '50%'],
          data: [],
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }
      ]
    };
    this.request.get("/echarts/members").then(res=>{
      optionMain.series[0].data=res.data
      optionPie.series[0].data = [
        {name: "第一季度", value: res.data[0]},
        {name: "第二季度", value: res.data[1]},
        {name: "第三季度", value: res.data[2]},
        {name: "第四季度", value: res.data[3]},
      ]
      optionPie.series[1].data = [
        {name: "第一季度", value: res.data[0]},
        {name: "第二季度", value: res.data[1]},
        {name: "第三季度", value: res.data[2]},
        {name: "第四季度", value: res.data[3]},
      ]
      myChart.setOption(optionMain)
      pieChart.setOption(optionPie)
    })
  }
}
</script>

<style scoped>

</style>