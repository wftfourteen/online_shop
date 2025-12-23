const { createApp } = Vue;
const { 
    ElContainer, ElHeader, ElMain, ElCheckbox, ElButton, ElInputNumber, ElIcon, 
    ElAvatar, ElDropdown, ElDropdownMenu, ElDropdownItem, ElEmpty, ElMessage, ElMessageBox
} = ElementPlus;
const { Delete } = ElementPlusIconsVue;

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
        ElCheckbox,
        ElButton,
        ElInputNumber,
        ElIcon,
        ElAvatar,
        ElDropdown,
        ElDropdownMenu,
        ElDropdownItem,
        ElEmpty,
        Delete
    },
    data() {
        return {
            userInfo: {},
            loading: false,
            cartItems: []
        };
    },
    computed: {
        selectedCount() {
            return this.cartItems.filter(item => item.selected).length;
        },
        totalPrice() {
            return this.cartItems
                .filter(item => item.selected)
                .reduce((sum, item) => sum + item.price * item.quantity, 0);
        }
    },
    mounted() {
        this.userInfo = checkAuth();
        if (this.userInfo) {
            this.loadCart();
        }
    },
    methods: {
        async loadCart() {
            this.loading = true;
            try {
                const res = await API.getCartList();
                if (res.code === 1 && res.data) {
                    this.cartItems = (res.data || []).map(item => ({
                        ...item,
                        selected: true
                    }));
                }
            } catch (error) {
                ElMessage.error('加载购物车失败');
            } finally {
                this.loading = false;
            }
        },
        
        async updateQuantity(item) {
            try {
                await API.updateCartItem(item.cartId, item.quantity);
                this.updateTotal();
            } catch (error) {
                ElMessage.error('更新数量失败');
                this.loadCart(); // 重新加载
            }
        },
        
        removeItem(cartId) {
            ElMessageBox.confirm('确定要删除该商品吗？', '提示', {
                type: 'warning'
            }).then(() => {
                return API.deleteCartItem(cartId);
            }).then(() => {
                ElMessage.success('删除成功');
                this.loadCart();
            }).catch((err) => {
                if (err !== 'cancel') {
                    ElMessage.error('删除失败');
                }
            });
        },
        
        updateTotal() {
            // 计算属性会自动更新
        },
        
        async checkout() {
            const selectedItems = this.cartItems.filter(item => item.selected);
            if (selectedItems.length === 0) {
                ElMessage.warning('请选择要结算的商品');
                return;
            }
            
            // 检查选中商品是否有库存
            for (const item of selectedItems) {
                if (item.stock < item.quantity) {
                    ElMessage.error(`商品 "${item.name}" 库存不足，当前库存：${item.stock}`);
                    return;
                }
            }
            
            const orderData = {
                items: selectedItems.map(item => ({
                    productId: item.productId,
                    quantity: item.quantity,
                    cartId: item.cartId,
                    name: item.name,
                    mainImage: item.mainImage,
                    price: item.price
                }))
            };
            sessionStorage.setItem('orderData', JSON.stringify(orderData));
            window.location.href = './checkout.html';
        },
        
        goShopping() {
            window.location.href = './customer.html';
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
