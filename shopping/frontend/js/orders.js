const { createApp } = Vue;
const { 
    ElContainer, ElHeader, ElMain, ElButton, ElTag, ElRadioGroup, ElRadioButton, 
    ElEmpty, ElAvatar, ElDropdown, ElDropdownMenu, ElDropdownItem, ElIcon, 
    ElMessage, ElMessageBox 
} = ElementPlus;
const { ShoppingCart } = ElementPlusIconsVue;

function checkAuth() {
    const token = localStorage.getItem('token');
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
    
    if (!token || userInfo.role !== 1) {
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
        ElButton,
        ElTag,
        ElRadioGroup,
        ElRadioButton,
        ElEmpty,
        ElAvatar,
        ElDropdown,
        ElDropdownMenu,
        ElDropdownItem,
        ElIcon,
        ShoppingCart
    },
    data() {
        return {
            userInfo: {},
            loading: false,
            orders: [],
            filterStatus: null,
            cartCount: 0
        };
    },
    mounted() {
        this.userInfo = checkAuth();
        if (this.userInfo) {
            this.loadOrders();
            this.loadCartCount();
        }
    },
    methods: {
        async loadOrders() {
            this.loading = true;
            try {
                const res = await API.getOrderList(this.filterStatus);
                if (res.code === 1 && res.data) {
                    this.orders = res.data || [];
                }
            } catch (error) {
                ElMessage.error('加载订单列表失败');
            } finally {
                this.loading = false;
            }
        },
        
        async loadCartCount() {
            try {
                const res = await API.getCartList();
                if (res.code === 1 && res.data) {
                    const items = res.data || [];
                    this.cartCount = items.reduce((sum, item) => sum + (item.quantity || 0), 0);
                }
            } catch (error) {
                // 忽略错误
            }
        },
        
        async payOrder(orderId) {
            try {
                const res = await API.payOrder(orderId, 'alipay');
                if (res.code === 1) {
                    ElMessage.success('支付成功！');
                    sessionStorage.setItem('paymentSuccess', JSON.stringify(res.data));
                    setTimeout(() => {
                        window.location.href = './payment-success.html';
                    }, 1000);
                } else {
                    ElMessage.error(res.msg || '支付失败');
                }
            } catch (error) {
                ElMessage.error('支付失败');
            }
        },
        
        async cancelOrder(orderId) {
            ElMessageBox.confirm('确定要取消该订单吗？', '提示', {
                type: 'warning'
            }).then(async () => {
                try {
                    const res = await API.cancelOrder(orderId);
                    if (res.code === 1) {
                        ElMessage.success('订单已取消');
                        this.loadOrders();
                    } else {
                        ElMessage.error(res.msg || '取消订单失败');
                    }
                } catch (error) {
                    ElMessage.error('取消订单失败');
                }
            }).catch(() => {});
        },
        
        async confirmReceipt(orderId) {
            ElMessageBox.confirm('确认已收到商品吗？', '提示', {
                type: 'warning'
            }).then(async () => {
                try {
                    const res = await API.confirmReceipt(orderId);
                    if (res.code === 1) {
                        ElMessage.success('确认收货成功');
                        this.loadOrders();
                    } else {
                        ElMessage.error(res.msg || '确认收货失败');
                    }
                } catch (error) {
                    ElMessage.error('确认收货失败');
                }
            }).catch(() => {});
        },
        
        viewDetail(orderId) {
            ElMessage.info('订单详情功能待开发');
        },
        
        getStatusType(status) {
            const types = { 1: 'warning', 2: 'info', 3: 'success', 4: '', 5: 'danger' };
            return types[status] || '';
        },
        
        getStatusText(status) {
            const texts = { 1: '待支付', 2: '已支付', 3: '已发货', 4: '已完成', 5: '已取消' };
            return texts[status] || '未知';
        },
        
        getProductImage(imageUrl) {
            if (!imageUrl) return '/default-product.png';
            if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) return imageUrl;
            if (imageUrl.includes('oss') || imageUrl.includes('aliyuncs')) return imageUrl;
            return API_CONFIG.BASE_URL + imageUrl;
        },
        
        getAvatarUrl(avatarUrl) {
            if (!avatarUrl) return '/default-avatar.png';
            if (avatarUrl.startsWith('http://') || avatarUrl.startsWith('https://')) return avatarUrl;
            if (avatarUrl.includes('oss') || avatarUrl.includes('aliyuncs')) return avatarUrl;
            return API_CONFIG.BASE_URL + avatarUrl;
        },
        
        handleImageError(event) {
            event.target.src = '/default-product.png';
        },
        
        formatDate(dateStr) {
            if (!dateStr) return '';
            const date = new Date(dateStr);
            return date.toLocaleString('zh-CN');
        },
        
        goShopping() {
            window.location.href = './customer.html';
        },
        
        goToCart() {
            window.location.href = './cart.html';
        },
        
        handleCommand(command) {
            if (command === 'profile') {
                window.location.href = './profile.html';
            } else if (command === 'orders') {
                window.location.href = './orders.html';
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
