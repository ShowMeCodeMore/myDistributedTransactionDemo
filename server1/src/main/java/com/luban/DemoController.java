package com.luban;

@RestController
@RequestMapping("server1")
public class DemoController {

    @Autowired
    private DemoService demoService;

    @RequestMapping(value = "test")
    public void test() {
        demoService.test();
    }
}
