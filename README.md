# ЛР1, этап 1

Предметная область 1: лабораторные образцы и измерения по протоколу.
Вариант 4: для хранения используется `TreeMap<Long, ...>`.

## Требования
- Java 17+
- Gradle

## Сборка
```bash
gradle build
```

## Запуск
```bash
gradle run
```

## Поддерживаемые команды
- `sample_add`
- `sample_list [--status ACTIVE|ARCHIVED] [--mine]`
- `sample_show <id>`
- `sample_update <id> field=value ...`
- `sample_archive <id>`
- `meas_add <sample_id>`
- `meas_list <sample_id> [--param PH|CONDUCTIVITY|TURBIDITY|NITRATE] [--last N]`
- `meas_stats <sample_id> <param>`
- `prot_create`
- `prot_apply <protocol_id> <sample_id>`

Для выхода из программы введите `exit`.
