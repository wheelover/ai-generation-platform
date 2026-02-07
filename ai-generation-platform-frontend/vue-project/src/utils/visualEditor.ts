/**
 * 可视化编辑器工具类
 * 负责管理iframe内的可视化编辑功能
 */
export interface ElementInfo {
  tagName: string
  id: string
  className: string
  textContent: string
  selector: string
  pagePath: string
  rect: {
    top: number
    left: number
    width: number
    height: number
  }
}

export interface VisualEditorOptions {
  onElementSelected?: (elementInfo: ElementInfo) => void
  onElementHover?: (elementInfo: ElementInfo) => void
}

export class VisualEditor {
  iframe: HTMLIFrameElement | null = null
  private isEditMode = false
  private options: VisualEditorOptions

  constructor(options: VisualEditorOptions = {}) {
    this.options = options
  }

  /**
   * 初始化编辑器
   */
  init(iframe: HTMLIFrameElement) {
    this.iframe = iframe
  }

  /**
   * 开启编辑模式
   */
  enableEditMode() {
    if (!this.iframe) {
      return
    }
    this.isEditMode = true
    this.injectEditScript()
  }

  /**
   * 关闭编辑模式
   */
  disableEditMode() {
    this.isEditMode = false
    this.sendMessageToIframe({
      type: 'TOGGLE_EDIT_MODE',
      editMode: false,
    })
    // 清除所有编辑状态
    this.sendMessageToIframe({
      type: 'CLEAR_ALL_EFFECTS',
    })
  }

  /**
   * 切换编辑模式
   */
  toggleEditMode() {
    if (this.isEditMode) {
      this.disableEditMode()
    } else {
      this.enableEditMode()
    }
    return this.isEditMode
  }

  /**
   * 强制同步状态并清理
   */
  syncState() {
    if (!this.isEditMode) {
      this.sendMessageToIframe({
        type: 'CLEAR_ALL_EFFECTS',
      })
    }
  }

  /**
   * 清除选中的元素
   */
  clearSelection() {
    this.sendMessageToIframe({
      type: 'CLEAR_SELECTION',
    })
  }

  /**
   * iframe 加载完成时调用
   */
  onIframeLoad() {
    if (this.isEditMode) {
      setTimeout(() => {
        this.injectEditScript()
      }, 500)
    } else {
      // 确保非编辑模式时清理状态
      setTimeout(() => {
        this.syncState()
      }, 500)
    }
  }

  /**
   * 处理来自 iframe 的消息
   */
  handleIframeMessage(event: MessageEvent) {
    const { type, data } = event.data
    switch (type) {
      case 'ELEMENT_SELECTED':
        if (this.options.onElementSelected && data.elementInfo) {
          this.options.onElementSelected(data.elementInfo)
        }
        break
      case 'ELEMENT_HOVER':
        if (this.options.onElementHover && data.elementInfo) {
          this.options.onElementHover(data.elementInfo)
        }
        break
    }
  }

  /**
   * 向 iframe 发送消息
   */
  private sendMessageToIframe(message: Record<string, any>) {
    if (this.iframe?.contentWindow) {
      this.iframe.contentWindow.postMessage(message, '*')
    }
  }

  /**
   * 注入编辑脚本到 iframe
   */
  private injectEditScript() {
    console.log('Injecting edit script...');
    if (!this.iframe) {
      console.error('No iframe available for script injection');
      return;
    }

    const waitForIframeLoad = () => {
      try {
        console.log('Waiting for iframe to load...');
        console.log('Iframe contentWindow:', this.iframe!.contentWindow);
        console.log('Iframe contentDocument:', this.iframe!.contentDocument);
        
        if (this.iframe!.contentWindow && this.iframe!.contentDocument) {
          console.log('Iframe is loaded, proceeding with script injection');
          
          // 检查是否已经注入过脚本
          if (this.iframe!.contentDocument.getElementById('visual-edit-script')) {
            console.log('Script already injected, sending toggle edit mode message');
            this.sendMessageToIframe({
              type: 'TOGGLE_EDIT_MODE',
              editMode: true,
            });
            return;
          }

          console.log('Injecting new script...');
          const script = this.generateEditScript();
          const scriptElement = this.iframe!.contentDocument.createElement('script');
          scriptElement.id = 'visual-edit-script';
          scriptElement.textContent = script;
          
          try {
            this.iframe!.contentDocument.head.appendChild(scriptElement);
            console.log('Script injected successfully');
          } catch (error) {
            console.error('Error appending script element:', error);
          }
        } else {
          console.log('Iframe not ready, waiting 100ms...');
          setTimeout(waitForIframeLoad, 100);
        }
      } catch (error) {
        console.error('Error during script injection:', error);
      }
    }

    waitForIframeLoad();
  }

  /**
   * 生成编辑脚本内容
   */
  private generateEditScript() {
    return `
      (function() {
        console.log('Visual editor script loaded');
        let isEditMode = true;
        let currentHoverElement = null;
        let currentSelectedElement = null;

        function injectStyles() {
          console.log('Injecting styles...');
          if (document.getElementById('edit-mode-styles')) return;
          const style = document.createElement('style');
          style.id = 'edit-mode-styles';
          style.textContent = \`
            .edit-hover {
              outline: 2px dashed #1890ff !important;
              outline-offset: 2px !important;
              cursor: crosshair !important;
              transition: outline 0.2s ease !important;
              position: relative !important;
            }
            .edit-hover::before {
              content: '' !important;
              position: absolute !important;
              top: -4px !important;
              left: -4px !important;
              right: -4px !important;
              bottom: -4px !important;
              background: rgba(24, 144, 255, 0.02) !important;
              pointer-events: none !important;
              z-index: -1 !important;
            }
            .edit-selected {
              outline: 3px solid #52c41a !important;
              outline-offset: 2px !important;
              cursor: default !important;
              position: relative !important;
            }
            .edit-selected::before {
              content: '' !important;
              position: absolute !important;
              top: -4px !important;
              left: -4px !important;
              right: -4px !important;
              bottom: -4px !important;
              background: rgba(82, 196, 26, 0.03) !important;
              pointer-events: none !important;
              z-index: -1 !important;
            }
          \`;
          document.head.appendChild(style);
          console.log('Styles injected successfully');
        }

        // 生成元素选择器
        function generateSelector(element) {
          const path = [];
          let current = element;
          while (current && current !== document.body) {
            let selector = current.tagName.toLowerCase();
            if (current.id) {
              selector += '#' + current.id;
              path.unshift(selector);
              break;
            }
            if (current.className) {
              const classes = current.className.split(' ').filter(c => c && !c.startsWith('edit-'));
              if (classes.length > 0) {
                selector += '.' + classes.join('.');
              }
            }
            const siblings = Array.from(current.parentElement?.children || []);
            const index = siblings.indexOf(current) + 1;
            selector += ':nth-child(' + index + ')';
            path.unshift(selector);
            current = current.parentElement;
          }
          return path.join(' > ');
        }

        // 获取元素信息
        function getElementInfo(element) {
          const rect = element.getBoundingClientRect();
          // 获取 HTML 文件名后面的部分（查询参数和锚点）
          let pagePath = window.location.search + window.location.hash;
          // 如果没有查询参数和锚点，则显示为空
          if (!pagePath) {
            pagePath = '';
          }

          return {
            tagName: element.tagName,
            id: element.id,
            className: element.className,
            textContent: element.textContent?.trim().substring(0, 100) || '',
            selector: generateSelector(element),
            pagePath: pagePath,
            rect: {
              top: rect.top,
              left: rect.left,
              width: rect.width,
              height: rect.height
            }
          };
        }

        // 清除悬浮效果
        function clearHoverEffect() {
          if (currentHoverElement) {
            currentHoverElement.classList.remove('edit-hover');
            currentHoverElement = null;
          }
        }

        // 清除选中效果
        function clearSelectedEffect() {
          const selected = document.querySelectorAll('.edit-selected');
          selected.forEach(el => el.classList.remove('edit-selected'));
          currentSelectedElement = null;
        }

        let eventListenersAdded = false;

        function addEventListeners() {
          if (eventListenersAdded) return;
          console.log('Adding event listeners...');

          const mouseoverHandler = function(event) {
            if (!isEditMode) return;
            console.log('Mouse over element:', event.target);

            const target = event.target;
            if (target === currentHoverElement || target === currentSelectedElement) return;
            if (target === document.body || target === document.documentElement) return;
            if (target.tagName === 'SCRIPT' || target.tagName === 'STYLE') return;

            clearHoverEffect();
            target.classList.add('edit-hover');
            currentHoverElement = target;
          };

          const mouseoutHandler = function(event) {
            if (!isEditMode) return;

            const target = event.target;
            if (!event.relatedTarget || !target.contains(event.relatedTarget)) {
              clearHoverEffect();
            }
          };

          const clickHandler = function(event) {
            if (!isEditMode) return;
            console.log('Element clicked:', event.target);

            event.preventDefault();
            event.stopPropagation();

            const target = event.target;
            if (target === document.body || target === document.documentElement) return;
            if (target.tagName === 'SCRIPT' || target.tagName === 'STYLE') return;

            clearSelectedEffect();
            clearHoverEffect();

            target.classList.add('edit-selected');
            currentSelectedElement = target;

            const elementInfo = getElementInfo(target);
            try {
              window.parent.postMessage({
                type: 'ELEMENT_SELECTED',
                data: { elementInfo }
              }, '*');
              console.log('Element info sent:', elementInfo);
            } catch (error) {
              console.error('Error sending element info:', error);
            }
          };

          document.addEventListener('mouseover', mouseoverHandler, true);
          document.addEventListener('mouseout', mouseoutHandler, true);
          document.addEventListener('click', clickHandler, true);
          eventListenersAdded = true;
          console.log('Event listeners added successfully');
        }

        function setupEventListeners() {
          addEventListeners();
        }

        // 监听父窗口消息
        window.addEventListener('message', function(event) {
          try {
            console.log('Message received from parent:', event.data);
            const { type, editMode } = event.data;
            switch (type) {
              case 'TOGGLE_EDIT_MODE':
                isEditMode = editMode;
                console.log('Edit mode toggled to:', isEditMode);
                if (isEditMode) {
                  injectStyles();
                  setupEventListeners();
                  showEditTip();
                } else {
                  clearHoverEffect();
                  clearSelectedEffect();
                }
                break;
              case 'CLEAR_SELECTION':
                clearSelectedEffect();
                break;
              case 'CLEAR_ALL_EFFECTS':
                isEditMode = false;
                clearHoverEffect();
                clearSelectedEffect();
                const tip = document.getElementById('edit-tip');
                if (tip) tip.remove();
                break;
            }
          } catch (error) {
            console.error('Error handling message:', error);
          }
        });

        function showEditTip() {
          if (document.getElementById('edit-tip')) return;
          const tip = document.createElement('div');
          tip.id = 'edit-tip';
          tip.innerHTML = '🎯 编辑模式已开启<br/>悬浮查看元素，点击选中元素';
          tip.style.cssText = \`
            position: fixed;
            top: 20px;
            right: 20px;
            background: #1890ff;
            color: white;
            padding: 12px 16px;
            border-radius: 6px;
            font-size: 14px;
            z-index: 9999;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
            animation: fadeIn 0.3s ease;
          \`;
          const style = document.createElement('style');
          style.textContent = '@keyframes fadeIn { from { opacity: 0; transform: translateY(-10px); } to { opacity: 1; transform: translateY(0); } }';
          document.head.appendChild(style);
          document.body.appendChild(tip);
          setTimeout(() => {
            if (tip.parentNode) {
              tip.style.animation = 'fadeIn 0.3s ease reverse';
              setTimeout(() => tip.remove(), 300);
            }
          }, 3000);
        }
        
        // 初始化
        console.log('Initializing visual editor...');
        injectStyles();
        setupEventListeners();
        showEditTip();
        console.log('Visual editor initialized successfully');
      })();
    `
  }
}
