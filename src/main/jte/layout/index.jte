@param String title
@param gg.jte.Content content

<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <script type="module" src="/js/datastar.js"></script>
    <title>${title}</title>
    <style>
        .clock-container {
            display: flex;
            flex-wrap: wrap;
            gap: 20px;
            justify-content: center;
        }

        .clock-wrapper {
            text-align: center;
        }

        .clock {
            width: 120px;
            height: 120px;
            border: 3px solid #333;
            border-radius: 50%;
            position: relative;
            background: white;
            margin: 0 auto 10px;
        }

        .clock-center {
            width: 8px;
            height: 8px;
            background: #333;
            border-radius: 50%;
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            z-index: 10;
        }

        .hand {
            position: absolute;
            background: #333;
            transform-origin: bottom center;
            bottom: 50%;
            left: 50%;
            border-radius: 2px;
        }

        .hour-hand {
            width: 4px;
            height: 30px;
            margin-left: -2px;
        }

        .minute-hand {
            width: 3px;
            height: 40px;
            margin-left: -1.5px;
        }

        .second-hand {
            width: 2px;
            height: 45px;
            margin-left: -1px;
            background: #ff0000;
        }

        .clock-number {
            position: absolute;
            font-size: 12px;
            font-weight: bold;
        }

        .timezone-label {
            font-weight: bold;
            margin-bottom: 5px;
        }

        .time-display {
            font-size: 14px;
            color: #666;
        }
    </style>
</head>
<body>
<main>
    ${content}
</main>
</body>
</html>
