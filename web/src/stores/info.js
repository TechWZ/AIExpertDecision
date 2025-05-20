import { defineStore } from 'pinia'
import useUser from '@/stores/user'

const useInfo = defineStore('info', {
    state: () => ({
        count: 100,
        banners:[],
        recommends:[]
    }),
    getters: {
        // 4. getters中用到别的store中的数据
        showMessage(state) {
            // 1. 获取user info
            const userStore = useUser()

            // 2. 获取info

            //3. 拼接信息
            return `name:${userStore.name}-count:${state.count}`
        }
    },
    actions:{
        increment(state){
            console.log(state)
            this.count++
        },
        incrementNum(num){
            this.count += num
        },
        async fetchInfoMultidata(){//在使用第二种方法时，删除这一行的async关键词
            const res = await fetch('http://123.207.32.32:8000/home/multidata')
            const data = await res.json()
            console.log(data)

            this.banners = data.data.banner.list
            this.recommends = data.data.recommend.list

            return 'aaa'

            //第二种方法
            // return new Promise(async (resolveComponent, reject) => {
            //     const res = await fetch('http://123.207.32.32:8000/home/multidata')
            //     const data = await res.json()

            //     this.banners = data.data.banner.list
            //     this.recommends = data.data.recommend.list

            //     resolve('bbb')
            // })
        }
    }
})

export default useInfo