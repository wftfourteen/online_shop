const { createApp } = Vue;
const { ElContainer, ElHeader, ElMain, ElMenu, ElMenuItem, ElTable, ElTableColumn, 
    ElButton, ElTag, ElDialog, ElForm, ElFormItem, ElInput, ElInputNumber, ElAvatar,
    ElDropdown, ElDropdownMenu, ElDropdownItem, ElMessage, ElMessageBox, ElUpload } = ElementPlus;
const { Goods, List, Delete } = ElementPlusIconsVue;

function checkAuth() {
    const token = localStorage.getItem('token');
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
    
    if (!token || userInfo.role !== 2) {
        window.location.href = './login.html';
        return null;
    }
    return userInfo;
}

createApp({
    components: {
        ElContainer,
        ElHeader,
        ElMain,
        ElMenu,
        ElMenuItem,
        ElTable,
        ElTableColumn,
        ElButton,
        ElTag,
        ElDialog,
        ElForm,
        ElFormItem,
        ElInput,
        ElInputNumber,
        ElAvatar,
        ElDropdown,
        ElDropdownMenu,
        ElDropdownItem,
        ElUpload,
        ElIcon: ElementPlus.ElIcon,
        Goods,
        List,
        Delete
    },
    data() {
        return {
            userInfo: {},
            activeMenu: 'products',
            loading: false,
            products: [],
            orders: [],
            productDialogVisible: false,
            editingProduct: null,
            productForm: {
                name: '',
                price: 0,
                stock: 0,
                description: '',
                mainImage: '',
                detailImages: ''
            },
            detailImageList: [],
            mainImageFile: null,
            detailImageFiles: []
        };
    },
    mounted() {
        this.userInfo = checkAuth();
        if (this.userInfo) {
            this.loadProducts();
        }
    },
    methods: {
        handleMenuSelect(key) {
            this.activeMenu = key;
            if (key === 'products') {
                this.loadProducts();
            } else if (key === 'orders') {
                this.loadOrders();
            }
        },
        
        async loadProducts() {
            this.loading = true;
            try {
                const res = await API.adminGetProducts({ page: 1, pageSize: 100 });
                if (res.code === 1 && res.data) {
                    this.products = res.data.list || [];
                }
            } catch (error) {
                ElMessage.error('加载商品列表失败');
            } finally {
                this.loading = false;
            }
        },
        
        async loadOrders() {
            this.loading = true;
            try {
                const res = await API.adminGetOrderList(1, 50);
                if (res.code === 1 && res.data) {
                    this.orders = res.data.list || [];
                }
            } catch (error) {
                ElMessage.error('加载订单列表失败');
            } finally {
                this.loading = false;
            }
        },
        
        showProductDialog() {
            this.editingProduct = null;
            this.productForm = { name: '', price: 0, stock: 0, description: '', mainImage: '', detailImages: '' };
            this.detailImageList = [];
            this.mainImageFile = null;
            this.detailImageFiles = [];
            this.productDialogVisible = true;
        },
        
        editProduct(product) {
            this.editingProduct = product;
            this.productForm = { ...product };
            // 解析详情图
            if (product.detailImages) {
                try {
                    this.detailImageList = JSON.parse(product.detailImages);
                } catch (e) {
                    this.detailImageList = [];
                }
            } else {
                this.detailImageList = [];
            }
            this.mainImageFile = null;
            this.detailImageFiles = [];
            this.productDialogVisible = true;
        },
        
        getImageUrl(url) {
            if (!url) return '/default-product.png';
            // 如果已经是完整URL（http或https开头），直接返回
            if (url.startsWith('http://') || url.startsWith('https://')) return url;
            // 如果是OSS URL，直接返回
            if (url.includes('oss')) return url;
            // 否则拼接基础URL
            return API_CONFIG.BASE_URL + url;
        },
        
        handleImageError(event) {
            event.target.src = '/default-product.png';
        },
        
        getAvatarUrl(avatar) {
            if (!avatar) return '/default-avatar.png';
            if (avatar.startsWith('http://') || avatar.startsWith('https://')) return avatar;
            if (avatar.includes('oss')) return avatar;
            return API_CONFIG.BASE_URL + avatar;
        },
        
        handleMainImageChange(file) {
            this.mainImageFile = file.raw;
        },
        
        handleDetailImagesChange(file, fileList) {
            this.detailImageFiles = fileList.map(f => f.raw).filter(f => f);
        },
        
        removeDetailImage(index) {
            this.detailImageList.splice(index, 1);
        },
        
        async saveProduct() {
            try {
                let productId;
                if (this.editingProduct) {
                    // 更新详情图JSON
                    this.productForm.detailImages = this.detailImageList.length > 0 
                        ? JSON.stringify(this.detailImageList) 
                        : '';
                    await API.adminUpdateProduct(this.editingProduct.productId, this.productForm);
                    productId = this.editingProduct.productId;
                    ElMessage.success('更新成功');
                } else {
                    const res = await API.adminAddProduct(this.productForm);
                    if (res.code === 1 && res.data) {
                        productId = res.data.productId;
                    }
                    ElMessage.success('添加成功');
                }
                
                // 上传主图
                if (this.mainImageFile && productId) {
                    try {
                        const uploadRes = await API.adminUploadMainImage(productId, this.mainImageFile);
                        if (uploadRes.code === 1 && uploadRes.data) {
                            this.productForm.mainImage = uploadRes.data.imageUrl;
                            ElMessage.success('主图上传成功');
                        }
                    } catch (error) {
                        ElMessage.warning('主图上传失败');
                    }
                }
                
                // 上传详情图
                if (this.detailImageFiles.length > 0 && productId) {
                    try {
                        await API.adminUploadDetailImages(productId, this.detailImageFiles);
                        ElMessage.success('详情图上传成功');
                        // 重新加载商品信息以获取最新的图片URL
                        const productRes = await API.adminGetProductDetail(productId);
                        if (productRes.code === 1 && productRes.data) {
                            if (productRes.data.detailImages) {
                                try {
                                    this.detailImageList = JSON.parse(productRes.data.detailImages);
                                } catch (e) {
                                    this.detailImageList = [];
                                }
                            }
                        }
                    } catch (error) {
                        ElMessage.warning('详情图上传失败');
                    }
                }
                
                this.productDialogVisible = false;
                this.loadProducts();
            } catch (error) {
                ElMessage.error('保存失败');
            }
        },
        
        async toggleStatus(product) {
            try {
                const newStatus = product.status === 1 ? 0 : 1;
                await API.adminUpdateProductStatus(product.productId, newStatus);
                ElMessage.success('操作成功');
                this.loadProducts();
            } catch (error) {
                ElMessage.error('操作失败');
            }
        },
        
        async deleteProduct(productId) {
            try {
                await ElMessageBox.confirm('确定要删除该商品吗？', '提示', { type: 'warning' });
                await API.adminDeleteProduct(productId);
                ElMessage.success('删除成功');
                this.loadProducts();
            } catch (error) {
                if (error !== 'cancel') {
                    ElMessage.error('删除失败');
                }
            }
        },
        
        viewOrder(order) {
            ElMessage.info('查看订单详情功能待开发');
        },
        
        async shipOrder(order) {
            try {
                const { value } = await ElMessageBox.prompt('请输入物流单号', '发货', {
                    inputPlaceholder: '请输入物流单号'
                });
                if (value) {
                    await API.adminShipOrder(order.orderId, value, '顺丰快递');
                    ElMessage.success('发货成功');
                    this.loadOrders();
                }
            } catch (error) {
                if (error !== 'cancel') {
                    ElMessage.error('发货失败');
                }
            }
        },
        
        getStatusType(status) {
            const types = { 1: 'warning', 2: 'info', 3: 'success', 4: '', 5: 'danger' };
            return types[status] || '';
        },
        
        getStatusText(status) {
            const texts = { 1: '待支付', 2: '已支付', 3: '已发货', 4: '已完成', 5: '已取消' };
            return texts[status] || '未知';
        },
        
        handleCommand(command) {
            if (command === 'profile') {
                window.location.href = './profile.html';
            } else if (command === 'logout') {
                this.handleLogout();
            }
        },
        
        async handleLogout() {
            try {
                await API.logout();
            } catch (error) {}
            localStorage.removeItem('token');
            localStorage.removeItem('userInfo');
            window.location.href = './index.html';
        }
    }
}).use(ElementPlus).mount('#app');
