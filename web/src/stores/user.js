import { defineStore } from 'pinia'

const useUser = defineStore('user', {
    state: () => ({
        name: 'why',
        age: 18,
        level: 100,
        count: 99,
        friends: [
            {id:111, name: 'why2'},
            {id:112, name: 'why3'},
            {id:113, name: 'why4'}
        ]
    }),

    getters: {
        // 1. 基本使用
        doubleCount(state) {
            return state.count * 2
        },

        // 2. 一个getter引入另外一个getter
        doubleCountAddOne(){
            // this是store实例
            return this.doubleCount + 1
        },

        // 3. getters也支持返回一个函数
        getFriendById(state) {
            return function(id){
                //第一种方式：
                // return state.friends.find()
                //第二种方式：
                for(let i=0; i< state.friends.length; i++){
                    const friend = state.friends[i]
                    if(friend.id === id){
                        return friend
                    }
                }
            }
        },
    }
})

export default useUser