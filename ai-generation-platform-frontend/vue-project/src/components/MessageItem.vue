<template>
  <div class="message-item" :class="message.role">
    <div class="message-content">
      <div class="message-header">
        <span class="message-author">{{ message.role === 'user' ? '我' : 'AI' }}</span>
        <span class="message-time">{{ message.timestamp }}</span>
      </div>
      <div class="message-body">
        <div v-if="message.role === 'ai'" class="ai-message" v-html="message.content"></div>
        <p v-else class="user-message">{{ message.content }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
interface Message {
  role: 'user' | 'ai'
  content: string
  timestamp: string
}

const props = defineProps<{
  message: Message
}>()
</script>

<style scoped>
.message-item {
  display: flex;
  animation: messageSlideIn 0.3s ease-out;
}

@keyframes messageSlideIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message-item.user {
  justify-content: flex-end;
}

.message-item.ai {
  justify-content: flex-start;
}

.message-content {
  max-width: 80%;
  padding: 16px 20px;
  border-radius: 16px;
  background: #ffffff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  position: relative;
  transition: all 0.3s ease;
}

.message-item.user .message-content {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #ffffff;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.message-item.user .message-content::after {
  content: '';
  position: absolute;
  bottom: 8px;
  right: -8px;
  width: 0;
  height: 0;
  border-left: 12px solid transparent;
  border-right: 12px solid transparent;
  border-top: 12px solid #764ba2;
  transform: rotate(45deg);
}

.message-item.ai .message-content {
  background: #f8f9fa;
  border: 1px solid #e9ecef;
}

.message-item.ai .message-content::after {
  content: '';
  position: absolute;
  bottom: 8px;
  left: -8px;
  width: 0;
  height: 0;
  border-left: 12px solid transparent;
  border-right: 12px solid transparent;
  border-top: 12px solid #f8f9fa;
  transform: rotate(-45deg);
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-size: 12px;
  color: #666666;
}

.message-item.user .message-header {
  color: rgba(255, 255, 255, 0.8);
}

.message-author {
  font-weight: 600;
  letter-spacing: 0.5px;
}

.message-body {
  font-size: 14px;
  line-height: 1.6;
  word-wrap: break-word;
}

.ai-message {
  white-space: pre-wrap;
  background: #ffffff;
  padding: 16px;
  border-radius: 12px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  font-size: 14px;
  border: 1px solid #e9ecef;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.05);
  line-height: 1.6;
}

/* Markdown styles */
.ai-message h1, .ai-message h2, .ai-message h3, .ai-message h4, .ai-message h5, .ai-message h6 {
  margin-top: 20px;
  margin-bottom: 10px;
  font-weight: 600;
  line-height: 1.25;
}

.ai-message h1 {
  font-size: 24px;
  border-bottom: 1px solid #eaecef;
  padding-bottom: 0.3em;
}

.ai-message h2 {
  font-size: 20px;
  border-bottom: 1px solid #eaecef;
  padding-bottom: 0.3em;
}

.ai-message h3 {
  font-size: 18px;
}

.ai-message h4 {
  font-size: 16px;
}

.ai-message h5 {
  font-size: 14px;
}

.ai-message h6 {
  font-size: 12px;
  color: #6a737d;
}

.ai-message p {
  margin-top: 0;
  margin-bottom: 16px;
}

.ai-message ul, .ai-message ol {
  margin-top: 0;
  margin-bottom: 16px;
  padding-left: 2em;
}

.ai-message li {
  margin-top: 0.25em;
}

.ai-message blockquote {
  border-left: 4px solid #dfe2e5;
  color: #6a737d;
  padding: 0 1em;
  margin: 0 0 16px 0;
}

.ai-message code {
  font-family: SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono', 'Courier New', monospace;
  background-color: rgba(27, 31, 35, 0.05);
  border-radius: 3px;
  padding: 0.2em 0.4em;
  font-size: 85%;
}

.ai-message pre {
  background-color: #f6f8fa;
  border-radius: 6px;
  padding: 16px;
  margin-top: 0;
  margin-bottom: 16px;
  overflow: auto;
  font-size: 13px;
}

.ai-message pre code {
  background-color: transparent;
  padding: 0;
  font-size: 100%;
}

.ai-message a {
  color: #0366d6;
  text-decoration: none;
}

.ai-message a:hover {
  text-decoration: underline;
}

.ai-message table {
  border-spacing: 0;
  border-collapse: collapse;
  margin-bottom: 16px;
  width: 100%;
  overflow: auto;
  display: block;
}

.ai-message table th {
  font-weight: 600;
  padding: 6px 13px;
  border: 1px solid #dfe2e5;
  background-color: #f6f8fa;
}

.ai-message table td {
  padding: 6px 13px;
  border: 1px solid #dfe2e5;
}

.ai-message table tr:nth-child(2n) {
  background-color: #f6f8fa;
}

/* Code block styles */
.ai-message code.hljs {
  display: block;
  overflow-x: auto;
  padding: 0.5em;
  color: #333;
  background: #f8f8f8;
}

.ai-message .hljs-keyword {
  color: #008000;
  font-weight: bold;
}

.ai-message .hljs-string {
  color: #ba2121;
}

.ai-message .hljs-number {
  color: #098658;
}

.ai-message .hljs-comment {
  color: #6a737d;
  font-style: italic;
}

@media (max-width: 480px) {
  .message-content {
    max-width: 90%;
    padding: 12px 16px;
  }
}
</style>