import i18n from "i18next";
import { initReactI18next } from "react-i18next";
import LanguageDetector from 'i18next-browser-languagedetector';
import en_us from "./locales/en_us.json";
import en_gb from "./locales/en_gb.json";
import zh_cn from "./locales/zh_cn.json";


i18n
    .use(initReactI18next) // passes i18n down to react-i18next
    .use(LanguageDetector)
    .init({
        detection:{
            order:['navigator', 'path', "querystring"]
        },
        // the translations
        // (tip move them in a JSON file and import them,
        // or even better, manage them via a UI: https://react.i18next.com/guides/multiple-translation-files#manage-your-translations-with-a-management-gui)
        resources: {
            "en-US": { translation: en_us },
            "en-GB": { translation: en_gb },
            "zh-CN": { translation: zh_cn },
        },
        fallbackLng: "zh-CN",
        interpolation: {
            escapeValue: false // react already safes from xss => https://www.i18next.com/translation-function/interpolation#unescape
        }
    });

export default i18n;