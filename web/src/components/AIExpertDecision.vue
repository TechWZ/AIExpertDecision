<script setup>
import ChatHistory from './ChatHistory.vue';
import Chat from './Chat.vue';
</script>
<template>
    <el-row style="height: 100vh;">
        <el-col :span="4" style="height: 100%; border-right: 1px solid #ebeef5;">
            <ChatHistory />
        </el-col>
        <el-col :span="20" style="height: 100%;">
            <Chat />
        </el-col>
    </el-row>
</template>
<style scoped>
.el-row {
    margin-left: 0 !important;
    margin-right: 0 !important;
}
</style>