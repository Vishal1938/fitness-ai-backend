curl -X POST http://localhost:8080/api/fitness/complete-plan \
  -H "Content-Type: application/json" \
  -d '{
    "goal": "muscle gain",
    "experience": "beginner",
    "daysPerWeek": "4",
    "targetCalories": "2800",
    "dietaryPreference": "balanced",
    "age": "25",
    "gender": "male",
    "currentWeight": "70kg",
    "targetWeight": "78kg",
    "height": "175cm"
  }'

  Expected response ====>
  {
    "workoutPlan": "1. WORKOUT PLAN:\n\n=== DAY 1: CHEST & TRICEPS ===\n- Bench Press: 3 sets x 8-10 reps...",
    "mealPlan": "2. MEAL PLAN:\n\n=== BREAKFAST (7:00 AM) - 650 calories ===\n- Oatmeal: 80g\n- Eggs: 3 whole...",
    "supplementRecommendations": "3. SUPPLEMENT RECOMMENDATIONS:\n\n=== ESSENTIAL SUPPLEMENTS ===\n1. Whey Protein...",
    "summary": "Complete fitness plan generated successfully with all three components",
    "processingTimeMs": 8543
  }






  curl -X POST http://localhost:8080/api/fitness/complete-plan \
    -H "Content-Type: application/json" \
    -d '{
      "goal": "fat loss",
      "experience": "advanced",
      "daysPerWeek": "6",
      "targetCalories": "2000",
      "dietaryPreference": "vegetarian",
      "age": "32",
      "gender": "female",
      "currentWeight": "68kg",
      "targetWeight": "62kg",
      "height": "165cm"
    }'


    curl -X POST http://localhost:8080/api/fitness/complete-plan \
      -H "Content-Type: application/json" \
      -d '{
        "goal": "strength training",
        "experience": "intermediate",
        "daysPerWeek": "5",
        "targetCalories": "2600",
        "dietaryPreference": "vegan",
        "age": "30",
        "gender": "male",
        "currentWeight": "80kg",
        "targetWeight": "80kg",
        "height": "178cm"
      }'

      curl -X POST http://localhost:8080/api/fitness/complete-plan \
        -H "Content-Type: application/json" \
        -d '{
          "goal": "endurance improvement",
          "experience": "beginner",
          "daysPerWeek": "4",
          "targetCalories": "2200",
          "dietaryPreference": "keto",
          "age": "35",
          "gender": "female",
          "currentWeight": "65kg",
          "targetWeight": "65kg",
          "height": "170cm"
        }'


        curl -X POST http://localhost:8080/api/fitness/complete-plan \
          -H "Content-Type: application/json" \
          -d '{
            "goal": "general fitness",
            "experience": "beginner",
            "daysPerWeek": "3",
            "targetCalories": "2500",
            "dietaryPreference": "balanced"
          }'

          {
            "info": {
              "name": "Complete Fitness Plan API",
              "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
            },
            "item": [
              {
                "name": "Get Complete Fitness Plan - Muscle Gain",
                "request": {
                  "method": "POST",
                  "header": [
                    {
                      "key": "Content-Type",
                      "value": "application/json"
                    }
                  ],
                  "body": {
                    "mode": "raw",
                    "raw": "{\n  \"goal\": \"muscle gain\",\n  \"experience\": \"beginner\",\n  \"daysPerWeek\": \"4\",\n  \"targetCalories\": \"2800\",\n  \"dietaryPreference\": \"balanced\",\n  \"age\": \"25\",\n  \"gender\": \"male\",\n  \"currentWeight\": \"70kg\",\n  \"targetWeight\": \"78kg\",\n  \"height\": \"175cm\"\n}"
                  },
                  "url": {
                    "raw": "http://localhost:8080/api/fitness/complete-plan",
                    "protocol": "http",
                    "host": ["localhost"],
                    "port": "8080",
                    "path": ["api", "fitness", "complete-plan"]
                  }
                }
              },
              {
                "name": "Get Complete Fitness Plan - Fat Loss",
                "request": {
                  "method": "POST",
                  "header": [
                    {
                      "key": "Content-Type",
                      "value": "application/json"
                    }
                  ],
                  "body": {
                    "mode": "raw",
                    "raw": "{\n  \"goal\": \"fat loss\",\n  \"experience\": \"intermediate\",\n  \"daysPerWeek\": \"5\",\n  \"targetCalories\": \"2000\",\n  \"dietaryPreference\": \"vegetarian\",\n  \"age\": \"30\",\n  \"gender\": \"female\",\n  \"currentWeight\": \"68kg\",\n  \"targetWeight\": \"62kg\",\n  \"height\": \"165cm\"\n}"
                  },
                  "url": {
                    "raw": "http://localhost:8080/api/fitness/complete-plan",
                    "protocol": "http",
                    "host": ["localhost"],
                    "port": "8080",
                    "path": ["api", "fitness", "complete-plan"]
                  }
                }
              }
            ]



          {
            "scheduleType": "ONE_TIME | RECURRING",
            "scheduledDateTime": "2025-11-30T10:00:00",  // ONE_TIME only
            "cronExpression": "0 0 9 * * MON",            // RECURRING only
            "email": "recipient@example.com",
            "reportName": "My Report Name",
            "generatePdf": true,
            "sendEmail": true,
            "planRequest": {
              "goal": "muscle gain",
              "experience": "beginner",
              "daysPerWeek": "4",
              "targetCalories": "2500",
              "dietaryPreference": "balanced",
              "age": "25",
              "gender": "male",
              "currentWeight": "70kg",
              "targetWeight": "75kg",
              "height": "175cm"
            }
          }


          curl -X POST http://localhost:8080/api/scheduler/schedule \
            -H "Content-Type: application/json" \
            -d '{
              "scheduleType": "RECURRING",
              "cronExpression": "0 0 9 * * MON",
              "email": "test@example.com",
              "reportName": "Weekly Plan",
              "generatePdf": true,
              "sendEmail": true,
              "planRequest": {
                "goal": "fat loss",
                "experience": "intermediate",
                "daysPerWeek": "5",
                "targetCalories": "2000",
                "dietaryPreference": "vegetarian"
              }
            }'


            {
              "scheduleType": "ONE_TIME | RECURRING",
              "scheduledDateTime": "2025-11-30T10:00:00",  // ONE_TIME only
              "cronExpression": "0 0 9 * * MON",            // RECURRING only
              "email": "recipient@example.com",
              "reportName": "My Report Name",
              "generatePdf": true,
              "sendEmail": true,
              "planRequest": {
                "goal": "muscle gain",
                "experience": "beginner",
                "daysPerWeek": "4",
                "targetCalories": "2500",
                "dietaryPreference": "balanced",
                "age": "25",
                "gender": "male",
                "currentWeight": "70kg",
                "targetWeight": "75kg",
                "height": "175cm"
              }
            }