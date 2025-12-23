const { createApp } = Vue;
const { ElButton, ElContainer, ElHeader, ElMain, ElCard, ElRow, ElCol, ElIcon } = ElementPlus;
const { User, UserFilled, ShoppingBag, Truck, CreditCard } = ElementPlusIconsVue;

createApp({
    components: {
        ElButton,
        ElContainer,
        ElHeader,
        ElMain,
        ElCard,
        ElRow,
        ElCol,
        ElIcon,
        User,
        UserFilled,
        ShoppingBag,
        Truck,
        CreditCard
    },
    methods: {
        goToLogin() {
            window.location.href = './login.html';
        },
        goToRegister() {
            window.location.href = './register.html';
        }
    }
}).use(ElementPlus).mount('#app');
