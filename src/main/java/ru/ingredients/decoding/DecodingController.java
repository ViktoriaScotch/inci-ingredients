package ru.ingredients.decoding;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.ingredients.ingredient.IngredientDTO;

import java.util.List;
import java.util.Map;

@Controller
public class DecodingController {

    private final DecodingService decodingService;

    public DecodingController(DecodingService decodingService) {
        this.decodingService = decodingService;
    }

    @PostMapping("/decoding")
    public String postDecodingText(@RequestParam String text, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("text", text);
        return "redirect:/decoding";
    }

    @GetMapping(value = "/decoding")
    public String getDecoding(@ModelAttribute("text") String text, Model model) {
        List<IngredientDTO> allIngredients = decodingService.decode(text);
        List<IngredientDTO> foundIngredients = allIngredients.stream().filter(i -> i.getInci() != null).toList();
        Map<String, List<IngredientDTO>> ingByCat = decodingService.groupByCat(foundIngredients);

        model.addAttribute("text", text);
        model.addAttribute("ingByCat", ingByCat);
        model.addAttribute("allIngredients", allIngredients);
        model.addAttribute("foundIngredients", foundIngredients);
        return "decoding/decoding";
    }
}
