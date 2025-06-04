<script setup>
import Chat from './Chat.vue';
</script>
<template>
    <div class="app-container">
        <el-row class="main-row" :gutter="0">
            <el-col :span="24">
                <Chat />
            </el-col>
        </el-row>
    </div>
</template>
<style scoped>
.app-container {
    margin: 0;
    padding: 0;
}
</style>