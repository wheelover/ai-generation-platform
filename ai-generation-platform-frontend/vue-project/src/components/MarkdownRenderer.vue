<template>
  <div class="markdown-renderer" v-html="renderedContent"></div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'

const props = defineProps<{
  content: string
}>()

// 配置 markdown-it
const md: MarkdownIt = new MarkdownIt({
  html: true, // 允许 HTML 标签
  linkify: true, // 自动转换 URL
  typographer: true, // 启用一些语言中立的替换和引号美化
  highlight: function (str: string, lang: string): string {
    if (lang && hljs.getLanguage(lang)) {
      try {
        return `<pre class="hljs"><code>${hljs.highlight(str, { language: lang, ignoreIllegals: true }).value}</code></pre>`
      } catch (__) {}
    }
    return `<pre class="hljs"><code>${md.utils.escapeHtml(str)}</code></pre>`
  }
})

const renderedContent = computed(() => {
  if (!props.content) return ''
  return md.render(props.content)
})
</script>

<style scoped>
/* Markdown 基础样式 */
.markdown-renderer {
  line-height: 1.6;
  word-wrap: break-word;
  font-size: 14px;
}

.markdown-renderer :deep(h1),
.markdown-renderer :deep(h2),
.markdown-renderer :deep(h3),
.markdown-renderer :deep(h4),
.markdown-renderer :deep(h5),
.markdown-renderer :deep(h6) {
  margin-top: 20px;
  margin-bottom: 10px;
  font-weight: 600;
  line-height: 1.25;
}

.markdown-renderer :deep(h1) {
  font-size: 24px;
  border-bottom: 1px solid #eaecef;
  padding-bottom: 0.3em;
}

.markdown-renderer :deep(h2) {
  font-size: 20px;
  border-bottom: 1px solid #eaecef;
  padding-bottom: 0.3em;
}

.markdown-renderer :deep(h3) {
  font-size: 18px;
}

.markdown-renderer :deep(h4) {
  font-size: 16px;
}

.markdown-renderer :deep(h5) {
  font-size: 14px;
}

.markdown-renderer :deep(h6) {
  font-size: 12px;
  color: #6a737d;
}

.markdown-renderer :deep(p) {
  margin-top: 0;
  margin-bottom: 16px;
}

.markdown-renderer :deep(ul),
.markdown-renderer :deep(ol) {
  margin-top: 0;
  margin-bottom: 16px;
  padding-left: 2em;
}

.markdown-renderer :deep(li) {
  margin-top: 0.25em;
}

.markdown-renderer :deep(blockquote) {
  border-left: 4px solid #dfe2e5;
  color: #6a737d;
  padding: 0 1em;
  margin: 0 0 16px 0;
}

.markdown-renderer :deep(code) {
  font-family: SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono', 'Courier New', monospace;
  background-color: rgba(27, 31, 35, 0.05);
  border-radius: 3px;
  padding: 0.2em 0.4em;
  font-size: 85%;
}

.markdown-renderer :deep(pre) {
  background-color: #f6f8fa;
  border-radius: 6px;
  padding: 16px;
  margin-top: 0;
  margin-bottom: 16px;
  overflow: auto;
  font-size: 13px;
}

.markdown-renderer :deep(pre code) {
  background-color: transparent;
  padding: 0;
  font-size: 100%;
}

.markdown-renderer :deep(a) {
  color: #0366d6;
  text-decoration: none;
}

.markdown-renderer :deep(a:hover) {
  text-decoration: underline;
}

.markdown-renderer :deep(table) {
  border-spacing: 0;
  border-collapse: collapse;
  margin-bottom: 16px;
  width: 100%;
  overflow: auto;
  display: block;
}

.markdown-renderer :deep(table th) {
  font-weight: 600;
  padding: 6px 13px;
  border: 1px solid #dfe2e5;
  background-color: #f6f8fa;
}

.markdown-renderer :deep(table td) {
  padding: 6px 13px;
  border: 1px solid #dfe2e5;
}

.markdown-renderer :deep(table tr:nth-child(2n)) {
  background-color: #f6f8fa;
}

/* 代码块样式 */
.markdown-renderer :deep(pre.hljs) {
  background-color: #f6f8fa;
  padding: 16px;
  margin: 16px 0;
  border-radius: 6px;
  overflow-x: auto;
}

.markdown-renderer :deep(code.hljs) {
  color: #333;
  background: transparent;
  padding: 0;
}
</style>
