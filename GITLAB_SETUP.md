# GitLab CI Setup для DeepAgent

## 🎯 Чому GitLab?

- ✅ **Безкоштовно** - 400 хвилин CI/CD на місяць
- ✅ **Без платіжної карти** - не потрібна оплата
- ✅ **Автоматична збірка APK** - при кожному push
- ✅ **Artifacts зберігаються** - легко завантажити

---

## 📋 Покрокова Інструкція

### Крок 1: Створити GitLab Акаунт

1. Перейдіть на https://gitlab.com
2. Натисніть **"Register"** (або "Sign up")
3. Заповніть форму:
   - Username: `ivansnigur09` (або будь-який)
   - Email: ваш email
   - Password: створіть пароль
4. Підтвердіть email
5. Готово! ✅

---

### Крок 2: Створити Новий Проект

1. Після входу натисніть **"New project"**
2. Виберіть **"Create blank project"**
3. Заповніть:
   - Project name: `DeepAgent`
   - Visibility: **Public** (щоб не платити за CI minutes)
4. Натисніть **"Create project"**

---

### Крок 3: Push Код на GitLab

**Варіант A: Через Git (Рекомендую)**

```bash
cd /workspaces/DeepAgent

# Додати GitLab remote
git remote add gitlab https://gitlab.com/ВАШ_USERNAME/DeepAgent.git

# Push код
git push gitlab bootstrap
```

**Варіант B: Я Можу Зробити Push**

Дайте мені:
1. Ваш GitLab username
2. GitLab Personal Access Token (створимо нижче)

---

### Крок 4: Створити Personal Access Token

1. GitLab → Settings (правий верхній кут)
2. Access Tokens (ліва панель)
3. Натисніть **"Add new token"**
4. Заповніть:
   - Token name: `DeepAgent CI`
   - Expiration date: через 1 рік
   - Scopes: поставте галочки на:
     - ✅ `api`
     - ✅ `read_repository`
     - ✅ `write_repository`
5. Натисніть **"Create personal access token"**
6. **СКОПІЮЙТЕ ТОКЕН** (показується тільки раз!)

---

### Крок 5: Push Код (Якщо Я Роблю)

Дайте мені:
```
GitLab Username: ваш_username
GitLab Token: glpat-xxxxxxxxxxxxxxxxxxxx
```

Я виконаю:
```bash
git remote add gitlab https://oauth2:ВАШ_ТОКЕН@gitlab.com/ВАШ_USERNAME/DeepAgent.git
git push gitlab bootstrap
```

---

### Крок 6: Дочекатись Збірки

1. Після push перейдіть на GitLab
2. Ваш проект → **CI/CD** → **Pipelines**
3. Ви побачите pipeline що запустився
4. Зачекайте 10-15 хвилин (перша збірка довга)
5. Коли pipeline стане зеленим ✅ - готово!

---

### Крок 7: Завантажити APK

1. GitLab → Ваш проект
2. **CI/CD** → **Pipelines**
3. Клікніть на зелений pipeline ✅
4. Справа побачите **"Job artifacts"**
5. Клікніть **"Download"** біля `build_debug`
6. Завантажиться ZIP з APK файлом
7. Розпакуйте → `app-debug.apk`

---

## 🚀 Автоматизація

Після налаштування:
- Кожен `git push` автоматично збирає APK
- APK доступний в Artifacts
- Не потрібно нічого робити вручну

---

## 📱 Прямий Лінк на APK

Після першої збірки ви отримаєте лінк типу:
```
https://gitlab.com/USERNAME/DeepAgent/-/jobs/artifacts/bootstrap/download?job=build_debug
```

Цей лінк можна шарити - будь-хто зможе завантажити APK!

---

## 🔧 Troubleshooting

### Pipeline Failed (червоний ❌)
1. Клікніть на failed job
2. Подивіться логи
3. Зазвичай проблема з SDK - перезапустіть pipeline

### Довго збирається
- Перша збірка: 10-15 хвилин (завантажує SDK)
- Наступні збірки: 5-7 хвилин (кешується)

### Не бачу Artifacts
- Дочекайтесь зеленого pipeline ✅
- Artifacts з'являються тільки після успішної збірки

---

## 💡 Переваги GitLab

1. **Безкоштовно** - 400 хвилин/місяць
2. **Публічні проекти** - необмежені minutes
3. **Artifacts** - зберігаються 1 тиждень
4. **Швидко** - паралельні jobs
5. **Надійно** - рідко падає

---

## 📊 Порівняння

| Feature | GitHub Actions | GitLab CI |
|---------|---------------|-----------|
| Безкоштовно | 2000 хв/міс | 400 хв/міс |
| Потрібна карта | ✅ Так | ❌ Ні |
| Публічні repo | Необмежено | Необмежено |
| Artifacts | 90 днів | 30 днів |
| Швидкість | Швидко | Швидко |

---

## ✅ Готові Почати?

**Що потрібно від вас:**

1. Створити GitLab акаунт
2. Створити проект
3. Дати мені:
   - GitLab username
   - Personal Access Token

**Або:**

Якщо хочете самі - виконайте кроки 1-3, потім:
```bash
cd /workspaces/DeepAgent
git remote add gitlab https://gitlab.com/ВАШ_USERNAME/DeepAgent.git
git push gitlab bootstrap
```

---

## 🎉 Після Setup

Ви отримаєте:
- ✅ Автоматичну збірку APK
- ✅ Прямий лінк для завантаження
- ✅ Збірка при кожному push
- ✅ Безкоштовно назавжди

**Готові? Створюйте акаунт і давайте токен!** 🚀
