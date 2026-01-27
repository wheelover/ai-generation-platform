// 生成类型常量，对应后端 CodeGenTypeEnum 枚举

export const CodeGenType = {
  // 原生 HTML 模式
  HTML: {
    label: '原生 HTML 模式',
    value: 'html'
  },
  // 原生多文件模式
  MULTI_FILE: {
    label: '原生多文件模式',
    value: 'multi_file'
  }
}

// 生成类型选项，用于下拉选择
export const CodeGenTypeOptions = [
  {
    label: CodeGenType.HTML.label,
    value: CodeGenType.HTML.value
  },
  {
    label: CodeGenType.MULTI_FILE.label,
    value: CodeGenType.MULTI_FILE.value
  }
]

// 根据值获取标签
export const getCodeGenTypeLabel = (value: string): string => {
  switch (value) {
    case CodeGenType.HTML.value:
      return CodeGenType.HTML.label
    case CodeGenType.MULTI_FILE.value:
      return CodeGenType.MULTI_FILE.label
    default:
      return value
  }
}
