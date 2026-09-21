sed -i '/Map<String, Object> step = new HashMap<>();/i \        int order = 1;' src/main/java/com/tuckersoft/branchengine/service/PlaythroughService.java
sed -i '/step.put("nodeCode"/i \            step.put("order", order++);' src/main/java/com/tuckersoft/branchengine/service/PlaythroughService.java
