import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';

import zh_cn from './locales/zh-CN.json';
import en_GB from './locales/en-GB.json';

i18n.use(initReactI18next).init({
    resources: {
        'zh-CN': { translation: zh_cn },
        'en-GB': { translation: en_GB },
    },
    lng: 'en-GB',
    interpolation: {
        escapeValue: false // react already safes from xss
    }
});

export default i18n;